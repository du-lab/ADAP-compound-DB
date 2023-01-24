package org.dulab.adapcompounddb.site.services.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.repositories.SpectrumClusterRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.dulab.adapcompounddb.site.services.utils.DataUtils;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.dulab.adapcompounddb.utils.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.CompleteLinkage;
import smile.clustering.linkage.Linkage;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SpectrumMatchServiceImpl implements SpectrumMatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpectrumMatchServiceImpl.class);

    private final SpectrumRepository spectrumRepository;
    private final SpectrumMatchRepository spectrumMatchRepository;
    private final SpectrumClusterRepository spectrumClusterRepository;
    private final SubmissionRepository submissionRepository;

    private enum ColumnInformation {
        ID(0, "id"),
        Cluster_ID(1, "id"),
        NAME(2, "name"),
        COUNT(3, "size"),
        SCORE(4, "score"),
        AVERAGE_SIGNIFICANCE(5, "averageSignificance"),
        MINIMUM_SIGNIFICANCE(6, "minimumSignificance"),
        SPECIES_PVALUE(7,"diseasePValue"),
        SAMPLE_SOURCE_PVALUE(8, "speciesPValue"),
        DISEASE_PVALUE(9,"sampleSourcePValue"),
        MINIMUM_PVALUE(10, "minPValue"),
        CHROMATOGRAPHYTYPE(11, "chromatographyType");

        private int position;
        private String sortColumnName;

        ColumnInformation(final int position, final String sortColumnName) {
            this.position = position;
            this.sortColumnName = sortColumnName;
        }

        public int getPosition() {
            return position;
        }

        public String getSortColumnName() {
            return sortColumnName;
        }

        public static String getColumnNameFromPosition(final int position) {
            String columnName = null;
            for (final ColumnInformation columnInformation : ColumnInformation.values()) {
                if (position == columnInformation.getPosition()) {
                    columnName = columnInformation.getSortColumnName();
                }
            }
            return columnName;
        }
    }





    @Autowired
    public SpectrumMatchServiceImpl(final SpectrumRepository spectrumRepository,
                                    final SpectrumMatchRepository spectrumMatchRepository,
                                    final SpectrumClusterRepository spectrumClusterRepository,
                                    final SubmissionRepository submissionRepository) {

        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
        this.spectrumClusterRepository = spectrumClusterRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional
    @Override
    public void cluster(final float mzTolerance, final int minNumSpectra, final float scoreThreshold)
            throws EmptySearchResultException {

        final List<Long> clusterIds = new ArrayList<>();

        for (final ChromatographyType type : ChromatographyType.values()) {

            final Map<Long, Integer> spectrumIdToIndexMap = new HashMap<>();
            final List<Long> spectrumIds = new ArrayList<>();
            int count = 0;

            for (final Spectrum spectrum : spectrumRepository
                    //                    .findAllByConsensusFalseAndReferenceFalseAndChromatographyType(type)) {
                    .findSpectraForClustering(type)) {
                spectrumIdToIndexMap.put(spectrum.getId(), count++);
                spectrumIds.add(spectrum.getId());
            }

            if (count == 0) {
                continue;
            }

            final double[][] distanceMatrix = new double[count][count];
            Arrays.stream(distanceMatrix)
                    .forEach(a -> Arrays.fill(a, 1.0));

            LOGGER.info(String.format("Retrieving matches of %s...", type));
            for (final SpectrumMatch spectrumMatch : spectrumMatchRepository
                    .findAllByQuerySpectrumChromatographyType(type)) {

                final Integer queryIndex = spectrumIdToIndexMap.get(
                        spectrumMatch.getQuerySpectrum().getId());
                final Integer matchIndex = spectrumIdToIndexMap.get(
                        spectrumMatch.getMatchSpectrum().getId());

                if (queryIndex == null || matchIndex == null) {
                    continue;
                }

                final double distance = similarityToDistance(spectrumMatch.getScore());

                distanceMatrix[queryIndex][matchIndex] = distance;
                distanceMatrix[matchIndex][queryIndex] = distance;
            }

            // Complete Hierarchical Clustering
            LOGGER.info("Clustering of non-consensus and non-reference spectra...");
            final Linkage linkage = new CompleteLinkage(distanceMatrix);
            final HierarchicalClustering clustering = new HierarchicalClustering(linkage);
            final int[] labels = clustering.partition(similarityToDistance(scoreThreshold));

            final List<SpectrumCluster> clusters = new ArrayList<>();

            for (final int label : Arrays.stream(labels).distinct().toArray()) {

                final int[] indices = IntStream.range(0, count)
                        .filter(i -> labels[i] == label)
                        .toArray();

                if (indices.length < minNumSpectra) {
                    continue;
                }

                final SpectrumCluster cluster = new SpectrumCluster();
//                cluster.setSize(indices.length);

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

                // Calculate the diversity index
                final Set<DiversityIndex> diversityIndices = new HashSet<>();

                for (final SubmissionCategoryType categoryType : SubmissionCategoryType.values()) {

                    final double diversity = MathUtils.diversityIndex(
                            cluster.getSpectra()
                                    .stream()
                                    .map(Spectrum::getFile).filter(Objects::nonNull)
                                    .map(File::getSubmission).filter(Objects::nonNull)
                                    .map(s -> s.getCategory(categoryType))
                                    .collect(Collectors.toList()));

                    if (diversity > 0.0) {
                        final DiversityIndex diversityIndex = new DiversityIndex();
                        diversityIndex.setId(new DiversityIndexId(cluster, categoryType));
                        diversityIndex.setDiversity(diversity);
                        diversityIndices.add(diversityIndex);
                    }
                }
                cluster.setDiversityIndices(diversityIndices);

                // Calculate the significance statistics
                final DoubleSummaryStatistics significanceStats = cluster.getSpectra()
                        .stream()
                        .map(Spectrum::getSignificance)
                        .filter(Objects::nonNull)
                        .collect(Collectors.summarizingDouble(Double::doubleValue));

                if (significanceStats.getCount() > 0) {
                    cluster.setAveSignificance(significanceStats.getAverage());
                    cluster.setMinSignificance(significanceStats.getMin());
                    cluster.setMaxSignificance(significanceStats.getMax());
                }

                clusters.add(cluster);
            }

            LOGGER.info("Saving clusters to the database...");
            spectrumClusterRepository.saveAll(clusters);

            for (final SpectrumCluster cluster : clusters) {
                clusterIds.add(cluster.getId());
            }
        }

        LOGGER.info("Deleting old clusters...");
        spectrumClusterRepository.deleteByIdNotIn(clusterIds);

        LOGGER.info("Clustering is completed.");
    }

    private Spectrum getSpectrum(final long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(() -> new EmptySearchResultException(id));
    }

    private void addConsensusSpectrum(final ChromatographyType type, final SpectrumCluster cluster,
                                      final float mzTolerance) {

        final Peak[] peaks = cluster.getSpectra()
                .stream()
                .flatMap(s -> s.getPeaks().stream())
                .toArray(Peak[]::new);

        final double[][] mzDistance = new double[peaks.length][peaks.length];
        for (int i = 0; i < peaks.length; ++i) {
            for (int j = i + 1; j < peaks.length; ++j) {
                final double distance = Math.abs(peaks[i].getMz() - peaks[j].getMz());
                mzDistance[i][j] = distance;
                mzDistance[j][i] = distance;
            }
        }

        final int[] labels = new HierarchicalClustering(new CompleteLinkage(mzDistance)).partition(mzTolerance);

        final int[] uniqueLabels = Arrays.stream(labels)
                .distinct()
                .toArray();

        final Spectrum consensusSpectrum = new Spectrum();
        final List<Peak> consensusPeaks = new ArrayList<>(uniqueLabels.length);

        for (final int label : uniqueLabels) {

            final double mz = IntStream.range(0, labels.length)
                    .filter(i -> labels[i] == label)
                    .mapToDouble(i -> peaks[i].getMz())
                    .average()
                    .orElseThrow(() -> new IllegalStateException("Could not calculate average m/z value."));

            final double intensity = IntStream.range(0, labels.length)
                    .filter(i -> labels[i] == label)
                    .mapToDouble(i -> peaks[i].getIntensity())
                    .sum() / cluster.getSize();

            final Peak consensusPeak = new Peak();
            consensusPeak.setMz(mz);
            consensusPeak.setIntensity(intensity);
            consensusPeak.setSpectrum(consensusSpectrum);

            consensusPeaks.add(consensusPeak);
        }

        consensusSpectrum.setChromatographyType(type);
        consensusSpectrum.setConsensus(true);
        consensusSpectrum.setReference(false);
        consensusSpectrum.setCluster(cluster);
        consensusSpectrum.setPeaks(consensusPeaks);
        consensusSpectrum.setName(getName(cluster));

        cluster.setConsensusSpectrum(consensusSpectrum);
    }

    private double similarityToDistance(final double similarity) {
        return Math.min(1.0, Math.exp(-similarity));
    }

    private double distanceToSimilarity(final double distance) {
        return -Math.log(distance);
    }

    @Transactional
    @Override
    public List<SpectrumCluster> getAllClusters() {
        return MappingUtils.toList(spectrumClusterRepository.findAll());
    }

    @Transactional
    @Override
    public SpectrumCluster getCluster(final long id) {
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
     *
     * @param cluster instance of SpectrumCluster
     * @return the most frequent name
     */
    private String getName(final SpectrumCluster cluster) {

        String maxName = "";
        int maxCount = 0;
        final Map<String, Integer> nameCountMap = new HashMap<>();
        for (final Spectrum spectrum : cluster.getSpectra()) {
            final String name = spectrum.getName();
            final int count = nameCountMap.getOrDefault(name, 0) + 1;
            nameCountMap.put(name, count);
            if (count > maxCount) {
                maxCount = count;
                maxName = name;
            }
        }

        return maxName;
    }

    @Override
    public DataTableResponse findAllClusters(UserPrincipal user, ChromatographyType chromatographyType, String search,
                                             String species, String source, String disease,
                                             Integer start, Integer length, Integer column, String sortDirection) {

        final String sortColumn = ColumnInformation.getColumnNameFromPosition(column);

//        Pageable pageable;
//        if (sortColumn != null) {
//            final Sort sort = new Sort(Sort.Direction.fromString(sortDirection), sortColumn);
//            pageable = PageRequest.of(start / length, length, sort);
//        } else {
//            pageable = PageRequest.of(start / length, length);
//        }

        //get page format
        Pageable pageable = DataUtils.createPageable(start, length, sortColumn, sortDirection);
        Iterable<BigInteger> submissionIds = submissionRepository.findSubmissionIdsByUserAndSubmissionTags(
                user != null ? user.getId() : null, species, source, disease);

        // fetch x records at a time based on start page .
        Page<SpectrumClusterView> spectrumPage =
                spectrumClusterRepository.findClusters(chromatographyType, search, submissionIds, pageable);
        List<SearchResultDTO> dtoList = spectrumPage.stream()
                .map(SearchResultDTO::new)
                .collect(Collectors.toList());

        final DataTableResponse response = new DataTableResponse(dtoList);
        response.setRecordsTotal(spectrumPage.getTotalElements());
        response.setRecordsFiltered(spectrumPage.getTotalElements());
        return response;
    }

    @Override
    public void loadTagsofCluster(SpectrumCluster cluster) {
        try {
            for (Spectrum s : cluster.getSpectra()) {
                if (s != null) {
                    File f = s.getFile();
                    if (f != null) {
                        Submission sub = f.getSubmission();
                        if (sub != null) {
                            sub.getTags();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public List<SearchResultDTO> convertSpectrumMatchToClusterDTO(List<SpectrumMatch> matches) {
        List<SearchResultDTO> clusters = new ArrayList<>(matches.size());
        for (SpectrumMatch match : matches) {
            SearchResultDTO cluster = new SearchResultDTO();

            // Query spectrum
            cluster.setQuerySpectrumId(match.getQuerySpectrum().getId());
            cluster.setQuerySpectrumName(match.getQuerySpectrum().getName());

            // Match cluster
            SpectrumCluster matchedCluster = match.getMatchSpectrum().getCluster();
            cluster.setSpectrumId(matchedCluster.getId());
            cluster.setName(match.getMatchSpectrum().getName());
            cluster.setSize((int) matchedCluster.getSpectra().stream()
                    .map(Spectrum::getFile).filter(Objects::nonNull)
                    .map(File::getSubmission).filter(Objects::nonNull)
                    .distinct().count());
            cluster.setScore(match.getScore());
            matchedCluster.getSpectra().stream()
                    .map(Spectrum::getSignificance).filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .ifPresent(cluster::setAveSignificance);
            matchedCluster.getSpectra().stream()
                    .map(Spectrum::getSignificance).filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .min()
                    .ifPresent(cluster::setMinSignificance);
            matchedCluster.getSpectra().stream()
                    .map(Spectrum::getSignificance).filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .max()
                    .ifPresent(cluster::setMaxSignificance);
            cluster.setChromatographyTypeLabel(match.getMatchSpectrum().getChromatographyType().getLabel());
            cluster.setChromatographyTypePath(match.getMatchSpectrum().getChromatographyType().getIconPath());

            clusters.add(cluster);
        }
        return clusters;
    }



    @Override
    public List<SpectrumMatch> findAllSpectrumMatchByUserIdAndQuerySpectrums(Long userId, List<Long> spectrumIds) {
        return spectrumMatchRepository.findAllSpectrumMatchByUserIdAndQuerySpectrums( userId, spectrumIds);
    }


}
