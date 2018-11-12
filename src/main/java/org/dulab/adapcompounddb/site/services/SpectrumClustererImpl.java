package org.dulab.adapcompounddb.site.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.DistanceMatrixWrapper;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.SubmissionTag;
import org.dulab.adapcompounddb.models.entities.SubmissionTagId;
import org.dulab.adapcompounddb.site.repositories.SpectrumClusterRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.utils.MathUtils;
import org.dulab.jsparcehc.Matrix;
import org.dulab.jsparcehc.SparseHierarchicalClusterer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.CompleteLinkage;

@Service
public class SpectrumClustererImpl implements SpectrumClusterer {

    private static final Logger LOGGER = LogManager.getLogger(SpectrumClusterer.class);

    private final SpectrumRepository spectrumRepository;
    private final SpectrumMatchRepository spectrumMatchRepository;
    private final SpectrumClusterRepository spectrumClusterRepository;;

    @Autowired
    public SpectrumClustererImpl(final SpectrumRepository spectrumRepository,
            final SpectrumMatchRepository spectrumMatchRepository,
            final SpectrumClusterRepository spectrumClusterRepository) {

        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
        this.spectrumClusterRepository = spectrumClusterRepository;
    }

    @Transactional
    @Override
    public void removeAll() {
        LOGGER.info("Deleting old clusters...");
        spectrumClusterRepository.deleteAllEmptyClusters();
        LOGGER.info("Deleting old clusters is completed");
    }

    @Transactional
    @Override
    public void cluster(final ChromatographyType type, final int minNumSpectra, final float scoreTolerance, final float mzTolerance) {

        LOGGER.info(String.format("Clustering spectra of type \"%s\"...", type));

        final List<Spectrum> spectra = ServiceUtils.toList(spectrumRepository.findSpectraForClustering(type));

        final Matrix matrix = new DistanceMatrixWrapper(
                pageable -> spectrumMatchRepository.findByChromatographyType(type, pageable),
                spectra);

        if (matrix.getNumElements() == 0) {
            LOGGER.info(String.format("No matches found for spectra of type \"%s\".", type));
            return;
        }

        final SparseHierarchicalClusterer clusterer = new SparseHierarchicalClusterer(
                matrix, new org.dulab.jsparcehc.CompleteLinkage());
        clusterer.cluster(scoreTolerance);
        final Map<Integer, Integer> labelMap = clusterer.getLabels();

        final long[] uniqueLabels = labelMap.values()
                .stream()
                .mapToLong(Integer::longValue)
                .distinct()
                .toArray();

        LOGGER.info(String.format("Saving new clusters of type \"%s\" to the database...", type));

        for (final long label : uniqueLabels) {

            final Set<Long> spectrumIds = labelMap.entrySet()
                    .stream()
                    .filter(e -> e.getValue() == label)
                    .map(Map.Entry::getKey)
                    .map(i -> spectra.get(i).getId())
                    .collect(Collectors.toSet());

            if (spectrumIds.size() < minNumSpectra) {
                continue;
            }

            final SpectrumCluster cluster = createCluster(spectrumIds, mzTolerance);
            spectrumClusterRepository.save(cluster);
        }

        LOGGER.info(String.format("Clustering spectra of type \"%s\" is completed.", type));
    }

    private SpectrumCluster createCluster(final Set<Long> spectrumIds, final float mzTolerance)
            throws EmptySearchResultException {

        final SpectrumCluster cluster = new SpectrumCluster();

        final List<Spectrum> spectra = spectrumIds.stream()
                .map(this::findSpectrum)
                .peek(s -> s.setCluster(cluster))
                .collect(Collectors.toList());

        cluster.setSpectra(spectra);
        cluster.setSize(spectra.size());

        // Calculate diameter
        cluster.setDiameter(spectra
                .stream()
                .flatMap(s -> s.getMatches().stream())
                .filter(m -> spectrumIds.contains(m.getMatchSpectrum().getId()))
                .mapToDouble(SpectrumMatch::getScore)
                .min()
                .orElse(0.0));

        // Calculate the significance statistics
        final DoubleSummaryStatistics significanceStats = cluster.getSpectra()
                .stream()
                .map(Spectrum::getSignificance)
                .filter(Objects::nonNull)
                .map(Math::abs)
                .collect(Collectors.summarizingDouble(Double::doubleValue));

        if (significanceStats.getCount() > 0) {
            cluster.setAveSignificance(significanceStats.getAverage());
            cluster.setMinSignificance(significanceStats.getMin());
            cluster.setMaxSignificance(significanceStats.getMax());
        }

        // Calculate diversity
        setDiversityIndices(cluster);;

        final Spectrum consensusSpectrum = createConsensusSpectrum(spectra, mzTolerance);
        consensusSpectrum.setCluster(cluster);
        cluster.setConsensusSpectrum(consensusSpectrum);

        return cluster;
    }

    private Spectrum findSpectrum(final long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(() -> new EmptySearchResultException(id));
    }

    private void setDiversityIndices(final SpectrumCluster cluster) {

        final List<String> tagList = new ArrayList<>();

        cluster.getSpectra()
        .stream()
        .map(Spectrum::getFile).filter(Objects::nonNull)
        .map(File::getSubmission).filter(Objects::nonNull)
        .map(Submission::getTags)
        .collect(Collectors.toList())
        .stream()
        .map(l -> l.stream()
                .map(SubmissionTag::getId)
                .map(SubmissionTagId::getName)
                .collect(Collectors.toList()))
        .map(s -> tagList.addAll(s));

        final Map<String, List<String>> tagMap = new HashMap<>(); // source:<src1, src2, src1, src2>

        tagList.forEach(tag -> {
            final String[] arr = tag.split(":", 2);
            if(arr.length == 2) {
                final String key = arr[0].trim();
                final String value = arr[1].trim();

                List<String> valueList = tagMap.get(key);
                if(CollectionUtils.isEmpty(valueList)) {
                    valueList = new ArrayList<>();
                    tagMap.put(key, valueList);
                }
                valueList.add(value);
            }
        });

        Double minDiversity = Double.MAX_VALUE;
        Double maxDiversity = 0.0;
        Double avgDiversity = 0.0;

        for(final Entry<String, List<String>> entry : tagMap.entrySet()) {

            final double diversity = MathUtils.diversityIndex(entry.getValue());
            avgDiversity += diversity;
            if(diversity > maxDiversity) {
                maxDiversity = diversity;
            }
            if(diversity < minDiversity) {
                minDiversity = diversity;
            }
        };

        avgDiversity = avgDiversity / tagMap.size();

        cluster.setMinDiversity(minDiversity);
        cluster.setMaxDiversity(maxDiversity);

        cluster.setAveDiversity(avgDiversity);
    }

    /**
     * Creates a consensus spectrum by clustering all m/z values and calculating average intensities for each cluster
     *
     * @param spectra     list of spectra
     * @param mzTolerance maximum distance between m/z values in a cluster
     * @return consensus spectrum
     */
    private Spectrum createConsensusSpectrum(final List<Spectrum> spectra, final float mzTolerance) {

        final ChromatographyType type = spectra.stream()
                .map(Spectrum::getChromatographyType)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot determine chromatography type"));

        final Peak[] peaks = spectra.stream()
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
                    .sum() / spectra.size();

            final Peak consensusPeak = new Peak();
            consensusPeak.setMz(mz);
            consensusPeak.setIntensity(intensity);
            consensusPeak.setSpectrum(consensusSpectrum);

            consensusPeaks.add(consensusPeak);
        }

        consensusSpectrum.setChromatographyType(type);
        consensusSpectrum.setConsensus(true);
        consensusSpectrum.setReference(false);
        consensusSpectrum.setPeaks(consensusPeaks, true);
        consensusSpectrum.addProperty("Name", getName(spectra));

        spectra.stream()
        .map(Spectrum::getPrecursor)
        .filter(Objects::nonNull)
        .mapToDouble(Double::doubleValue)
        .average()
        .ifPresent(consensusSpectrum::setPrecursor);

        return consensusSpectrum;
    }


    /**
     * Selects the most frequent name in the cluster
     *
     * @param spectra list of spectra
     * @return the most frequent name
     */
    private String getName(final List<Spectrum> spectra) {

        String maxName = "";
        int maxCount = 0;
        final Map<String, Integer> nameCountMap = new HashMap<>();
        for (final Spectrum spectrum : spectra) {
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
}
