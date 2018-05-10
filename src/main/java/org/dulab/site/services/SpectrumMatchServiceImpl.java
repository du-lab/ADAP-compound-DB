package org.dulab.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.ChromatographyType;
import org.dulab.models.Hit;
import org.dulab.models.entities.*;
import org.dulab.models.search.CriteriaBlock;
import org.dulab.models.search.SetOperator;
import org.dulab.site.repositories.SpectrumClusterRepository;
import org.dulab.site.repositories.SpectrumMatchRepository;
import org.dulab.site.repositories.SpectrumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.CompleteLinkage;
import smile.clustering.linkage.Linkage;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SpectrumMatchServiceImpl implements SpectrumMatchService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SpectrumRepository spectrumRepository;
    private final SpectrumMatchRepository spectrumMatchRepository;
    private final SpectrumClusterRepository spectrumClusterRepository;

    @Autowired
    public SpectrumMatchServiceImpl(SpectrumRepository spectrumRepository,
                                    SpectrumMatchRepository spectrumMatchRepository,
                                    SpectrumClusterRepository spectrumClusterRepository) {
        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
        this.spectrumClusterRepository = spectrumClusterRepository;
    }

    @Transactional
    @Override
    public void fillSpectrumMatchTable(float mzTolerance, float scoreThreshold) {

        List<SpectrumMatch> spectrumMatches = new ArrayList<>();

        for (ChromatographyType chromatographyType : ChromatographyType.values()) {

            long startingTime = System.currentTimeMillis();

            Iterable<Spectrum> unmatchedSpectra =
                    spectrumRepository.findUnmatchedByChromatographyType(chromatographyType);
            long count = 0;

            for (Spectrum querySpectrum : unmatchedSpectra) {

                Iterable<Hit> hits = spectrumRepository.findSimilarSpectra(querySpectrum, mzTolerance, scoreThreshold);

                for (Hit hit : hits) {
                    SpectrumMatch match = new SpectrumMatch();
                    match.setQuerySpectrum(querySpectrum);
                    match.setMatchSpectrum(hit.getSpectrum());
                    match.setScore(hit.getScore());
                    spectrumMatches.add(match);
                }

                ++count;
            }

            long elapsedTime = System.currentTimeMillis() - startingTime;
            LOGGER.info(String.format("%d query spectra searched with average time %d milliseconds.",
                    count, count > 0 ? elapsedTime / count : 0));
        }

        spectrumMatchRepository.saveAll(spectrumMatches);

        LOGGER.info(String.format("Save %d matches to the database.", spectrumMatches.size()));
    }

    @Transactional
    @Override
    public void cluster(float mzTolerance, int minNumSpectra, float scoreThreshold)
            throws EmptySearchResultException {

        Map<Long, Integer> spectrumIdToIndexMap = new HashMap<>();
        List<Long> spectrumIds = new ArrayList<>();
        int count = 0;
        for (Spectrum spectrum : spectrumRepository.findAll()) {
            spectrumIdToIndexMap.put(spectrum.getId(), count++);
            spectrumIds.add(spectrum.getId());
        }

        double[][] distanceMatrix = new double[count][count];
        Arrays.stream(distanceMatrix)
                .forEach(a -> Arrays.fill(a, 1.0));

        for (SpectrumMatch spectrumMatch : spectrumMatchRepository.findAll()) {

            int queryIndex = spectrumIdToIndexMap.get(
                    spectrumMatch.getQuerySpectrum().getId());
            int matchIndex = spectrumIdToIndexMap.get(
                    spectrumMatch.getMatchSpectrum().getId());

            double distance = 1.0 - spectrumMatch.getScore();
            distanceMatrix[queryIndex][matchIndex] = distance;
            distanceMatrix[matchIndex][queryIndex] = distance;
        }

        // Complete Hierarchical Clustering
        Linkage linkage = new CompleteLinkage(distanceMatrix);
        HierarchicalClustering clustering = new HierarchicalClustering(linkage);
        int[] labels = clustering.partition(0.2);

        List<SpectrumCluster> clusters = new ArrayList<>();
        for (int label : Arrays.stream(labels).distinct().toArray()) {
            int[] indices = IntStream.range(0, count)
                    .filter(i -> labels[i] == label)
                    .toArray();

            if (indices.length < minNumSpectra) continue;

            SpectrumCluster cluster = new SpectrumCluster();
            cluster.setId(label);
            cluster.setSize(indices.length);

            cluster.setDiameter(Arrays
                    .stream(indices)
                    .mapToDouble(i -> Arrays
                            .stream(indices)
                            .mapToDouble(j -> distanceMatrix[i][j])
                            .max()
                            .orElse(0.0))
                    .max()
                    .orElse(0.0));

            cluster.setSpectra(Arrays
                    .stream(indices)
                    .mapToObj(i -> getSpectrum(spectrumIds.get(i)))
                    .collect(Collectors.toList()));

            cluster.getSpectra()
                    .forEach(s -> s.setCluster(cluster));

            addConsensusSpectrum(cluster, mzTolerance);

            clusters.add(cluster);
        }

        spectrumClusterRepository.saveAll(clusters);
        spectrumClusterRepository.deleteByIdNotIn(clusters
                .stream()
                .mapToLong(SpectrumCluster::getId)
                .toArray());
    }

    private Spectrum getSpectrum(long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(() -> new EmptySearchResultException(id));
    }

    private void addConsensusSpectrum(SpectrumCluster cluster, float mzTolerance) {

        Peak[] peaks = cluster.getSpectra()
                .stream()
                .flatMap(s -> s.getPeaks().stream())
                .toArray(Peak[]::new);

        double[][] mzDistance = new double[peaks.length][peaks.length];
        for (int i = 0; i < peaks.length; ++i)
            for (int j = i + 1; j <peaks.length; ++j) {
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
                    .sum() / cluster.getSize();

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
        nameProperty.setValue("Consensus Spectrum");
        nameProperty.setSpectrum(consensusSpectrum);

        consensusSpectrum.setConsensus(true);
        consensusSpectrum.setPeaks(consensusPeaks);
        consensusSpectrum.setProperties(Collections.singletonList(nameProperty));

        cluster.setConsensusSpectrum(consensusSpectrum);
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
}
