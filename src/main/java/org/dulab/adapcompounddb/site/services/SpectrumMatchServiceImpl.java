package org.dulab.adapcompounddb.site.services;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.repositories.SpectrumClusterRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.CompleteLinkage;
import smile.clustering.linkage.Linkage;

@Service
public class SpectrumMatchServiceImpl implements SpectrumMatchService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SpectrumRepository spectrumRepository;
    private final SpectrumMatchRepository spectrumMatchRepository;
    private final SpectrumClusterRepository spectrumClusterRepository;

    private final MathService mathService;

    private final Map<ChromatographyType, QueryParameters> queryParametersMap;

    @Autowired
    public SpectrumMatchServiceImpl(SpectrumRepository spectrumRepository,
                                    SpectrumMatchRepository spectrumMatchRepository,
                                    SpectrumClusterRepository spectrumClusterRepository,
                                    MathService mathService) {

        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
        this.spectrumClusterRepository = spectrumClusterRepository;
        this.mathService = mathService;

        QueryParameters gcQueryParameters = new QueryParameters()
                .setScoreThreshold(0.75)
                .setMzTolerance(0.01);

        QueryParameters lcQueryParameters = new QueryParameters()
                .setScoreThreshold(0.75)
                .setMzTolerance(0.01)
                .setPrecursorTolerance(0.01)
                .setRetTimeTolerance(0.5);

        this.queryParametersMap = new HashMap<>();
        this.queryParametersMap.put(ChromatographyType.GAS, gcQueryParameters);
        this.queryParametersMap.put(ChromatographyType.LIQUID_POSITIVE, lcQueryParameters);
        this.queryParametersMap.put(ChromatographyType.LIQUID_NEGATIVE, lcQueryParameters);
    }

//    @Transactional
//    @Override
//    public void fillSpectrumMatchTable(float mzTolerance, float scoreThreshold) {
//
//        List<SpectrumMatch> spectrumMatches = new ArrayList<>();
//
//        for (ChromatographyType chromatographyType : ChromatographyType.values()) {
//
//            QueryParameters params = queryParametersMap.get(chromatographyType);
//            if (params == null)
//                throw new IllegalStateException("Clustering query parameters are not specified.");
//
//            long startingTime = System.currentTimeMillis();
//
//            Iterable<Spectrum> unmatchedSpectra =
//                    spectrumRepository.findUnmatchedByChromatographyType(chromatographyType);
//
//            long count = 0;
//            for (Spectrum querySpectrum : unmatchedSpectra) {
//                spectrumMatches.addAll(
//                        spectrumRepository.spectrumSearch(
//                                SearchType.CLUSTERING, querySpectrum, params));
//                ++count;
//            }
//
//            long elapsedTime = System.currentTimeMillis() - startingTime;
//            LOGGER.info(String.format("%d query spectra searched with average time %d milliseconds.",
//                    count, count > 0 ? elapsedTime / count : 0));
//        }
//
//        spectrumMatchRepository.saveAll(spectrumMatches);
//
//        LOGGER.info(String.format("Save %d matches to the database.", spectrumMatches.size()));
//    }

    @Transactional
    @Override
    public void cluster(float mzTolerance, int minNumSpectra, float scoreThreshold)
            throws EmptySearchResultException {

        List<Long> clusterIds = new ArrayList<>();

        for (ChromatographyType type : ChromatographyType.values()) {

            Map<Long, Integer> spectrumIdToIndexMap = new HashMap<>();
            List<Long> spectrumIds = new ArrayList<>();
            int count = 0;

            for (Spectrum spectrum : spectrumRepository
                    .findAllByConsensusFalseAndReferenceFalseAndChromatographyType(type)) {
                spectrumIdToIndexMap.put(spectrum.getId(), count++);
                spectrumIds.add(spectrum.getId());
            }

            if (count == 0) continue;

            double[][] distanceMatrix = new double[count][count];
            Arrays.stream(distanceMatrix)
                    .forEach(a -> Arrays.fill(a, 1.0));

            LOGGER.info(String.format("Retrieving matches of %s...", type));
            for (SpectrumMatch spectrumMatch : spectrumMatchRepository
                    .findAllByQuerySpectrumChromatographyType(type)) {

                int queryIndex = spectrumIdToIndexMap.get(
                        spectrumMatch.getQuerySpectrum().getId());
                int matchIndex = spectrumIdToIndexMap.get(
                        spectrumMatch.getMatchSpectrum().getId());

                double distance = similarityToDistance(spectrumMatch.getScore());

                distanceMatrix[queryIndex][matchIndex] = distance;
                distanceMatrix[matchIndex][queryIndex] = distance;
            }

            // Complete Hierarchical Clustering
            LOGGER.info("Clustering of non-consensus and non-reference spectra...");
            Linkage linkage = new CompleteLinkage(distanceMatrix);
            HierarchicalClustering clustering = new HierarchicalClustering(linkage);
            int[] labels = clustering.partition(similarityToDistance(scoreThreshold));

            List<SpectrumCluster> clusters = new ArrayList<>();

            for (int label : Arrays.stream(labels).distinct().toArray()) {

                System.out.println(label + " " + type);

                int[] indices = IntStream.range(0, count)
                        .filter(i -> labels[i] == label)
                        .toArray();

                if (indices.length < minNumSpectra) continue;

                SpectrumCluster cluster = new SpectrumCluster();
                cluster.setSize(indices.length);

                cluster.setDiameter(distanceToSimilarity(Arrays
                        .stream(indices)
                        .mapToDouble(i -> Arrays
                                .stream(indices)
                                .mapToDouble(j -> distanceMatrix[i][j])
                                .max()
                                .orElse(0.0))
                        .max()
                        .orElse(0.0)));

                cluster.setSpectra(Arrays
                        .stream(indices)
                        .mapToObj(i -> getSpectrum(spectrumIds.get(i)))
                        .collect(Collectors.toList()));

                cluster.getSpectra()
                        .forEach(s -> s.setCluster(cluster));

                addConsensusSpectrum(type, cluster, mzTolerance);


                Set<DiversityIndex> diversityIndices = new HashSet<>();
                for (SubmissionCategoryType categoryType : SubmissionCategoryType.values()) {

                    double diversity = mathService.diversityIndex(
                            cluster.getSpectra()
                                    .stream()
                                    .map(Spectrum::getFile).filter(Objects::nonNull)
                                    .map(File::getSubmission).filter(Objects::nonNull)
                                    .map(s -> s.getCategory(categoryType))
                                    .collect(Collectors.toList()));

                    if (diversity > 0.0) {
                        DiversityIndex diversityIndex = new DiversityIndex();
                        diversityIndex.setId(new DiversityIndexId(cluster, categoryType));
                        diversityIndex.setDiversity(diversity);
                        diversityIndices.add(diversityIndex);
                    }
                }
                cluster.setDiversityIndices(diversityIndices);


                clusters.add(cluster);
            }

            LOGGER.info("Saving clusters to the database...");
            spectrumClusterRepository.saveAll(clusters);

            for (SpectrumCluster cluster : clusters)
                clusterIds.add(cluster.getId());
        }

        LOGGER.info("Deleting old clusters...");
        spectrumClusterRepository.deleteByIdNotIn(clusterIds);

        LOGGER.info("Clustering is completed.");
    }

    private Spectrum getSpectrum(long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(() -> new EmptySearchResultException(id));
    }

    private void addConsensusSpectrum(ChromatographyType type, SpectrumCluster cluster, float mzTolerance) {

        Peak[] peaks = cluster.getSpectra()
                .stream()
                .flatMap(s -> s.getPeaks().stream())
                .toArray(Peak[]::new);

        double[][] mzDistance = new double[peaks.length][peaks.length];
        for (int i = 0; i < peaks.length; ++i)
            for (int j = i + 1; j < peaks.length; ++j) {
                double distance = Math.abs(peaks[i].getMz() - peaks[j].getMz());
                mzDistance[i][j] = distance;
                mzDistance[j][i] = distance;
            }

        int[] labels = new HierarchicalClustering(new CompleteLinkage(mzDistance)).partition(mzTolerance);

        int[] uniqueLabels = Arrays.stream(labels)
                .distinct()
                .toArray();

        Spectrum consensusSpectrum = new Spectrum();
        List<Peak> consensusPeaks = new ArrayList<>(uniqueLabels.length);

        for (int label : uniqueLabels) {

            double mz = IntStream.range(0, labels.length)
                    .filter(i -> labels[i] == label)
                    .mapToDouble(i -> peaks[i].getMz())
                    .average()
                    .orElseThrow(() -> new IllegalStateException("Could not calculate average m/z value."));

            double intensity = IntStream.range(0, labels.length)
                    .filter(i -> labels[i] == label)
                    .mapToDouble(i -> peaks[i].getIntensity())
                    .sum() / cluster.getSize();

            Peak consensusPeak = new Peak();
            consensusPeak.setMz(mz);
            consensusPeak.setIntensity(intensity);
            consensusPeak.setSpectrum(consensusSpectrum);

            consensusPeaks.add(consensusPeak);
        }

        SpectrumProperty nameProperty = new SpectrumProperty();
        nameProperty.setName("Name");
        nameProperty.setValue(getName(cluster));
        nameProperty.setSpectrum(consensusSpectrum);

        consensusSpectrum.setChromatographyType(type);
        consensusSpectrum.setConsensus(true);
        consensusSpectrum.setReference(false);
        consensusSpectrum.setCluster(cluster);
        consensusSpectrum.setPeaks(consensusPeaks);
        consensusSpectrum.setProperties(Collections.singletonList(nameProperty));

        cluster.setConsensusSpectrum(consensusSpectrum);
    }

    private double similarityToDistance(double similarity) {
        return Math.min(1.0, Math.exp(-similarity));
    }

    private double distanceToSimilarity(double distance) {
        return -Math.log(distance);
    }

    @Transactional
    @Override
    public List<SpectrumCluster> getAllClusters() {
        return ServiceUtils.toList(spectrumClusterRepository.findAll());
    }

    @Transactional
    @Override
    public SpectrumCluster getCluster(long id) {
        return spectrumClusterRepository.findById(id)
                .orElseThrow(() -> new EmptySearchResultException(id));
    }

    @Transactional
    @Override
    public long getTotalNumberOfClusters() {
        return spectrumClusterRepository.count();
    }


    /**
     * Selects the most frequent name in the cluster
     * @param cluster instance of SpectrumCluster
     * @return the most frequent name
     */
    private String getName(SpectrumCluster cluster) {

        String maxName = "";
        int maxCount = 0;
        Map<String, Integer> nameCountMap = new HashMap<>();
        for (Spectrum spectrum : cluster.getSpectra()) {
            String name = spectrum.getName();
            int count = nameCountMap.getOrDefault(name, 0) + 1;
            nameCountMap.put(name, count);
            if (count > maxCount) {
                maxCount = count;
                maxName = name;
            }
        }

        return maxName;
    }
}
