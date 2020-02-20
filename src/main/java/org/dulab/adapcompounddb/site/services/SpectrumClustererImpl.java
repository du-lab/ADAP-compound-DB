package org.dulab.adapcompounddb.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.DistanceMatrixWrapper;
import org.dulab.adapcompounddb.models.dto.TagInfo;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.enums.MassSpectrometryType;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.*;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.dulab.jsparcehc.Matrix;
import org.dulab.jsparcehc.MatrixImpl;
import org.dulab.jsparcehc.SparseHierarchicalClusterer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpectrumClustererImpl implements SpectrumClusterer {

    private static final float MZ_TOLERANCE = 0.01F;

    private static final float SCORE_TOLERANCE = 0.25F;

    private static final int MIN_NUM_SPECTRA = 1;

    private static final double PEAK_INTENSITY_FRACTION = 0.005;

    private static final Logger LOGGER = LogManager.getLogger(SpectrumClusterer.class);

    private final SpectrumRepository spectrumRepository;
    private final SpectrumMatchRepository spectrumMatchRepository;
    private final SpectrumClusterRepository spectrumClusterRepository;
    private DistributionRepository distributionRepository;
    private DistributionService distributionService;

    private float progress = -0.1F;

    @Autowired
    public SpectrumClustererImpl(final SpectrumRepository spectrumRepository,
                                 final SpectrumMatchRepository spectrumMatchRepository,
                                 final DistributionRepository distributionRepository,
                                 final SpectrumClusterRepository spectrumClusterRepository,
                                 final DistributionService distributionService) {

        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
        this.spectrumClusterRepository = spectrumClusterRepository;
        this.distributionRepository = distributionRepository;
        this.distributionService = distributionService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void removeAll() {
        LOGGER.info("Deleting old clusters...");
        try {
            spectrumClusterRepository.deleteAllEmptyClusters();
            spectrumClusterRepository.deleteAllConsensusSpectra();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Deleting old clusters is completed");
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    public void setProgress(final float progress) {
        this.progress = progress;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void cluster() {

        long createClusterTotalTime = 0;
        long saveClusterTotalTime = 0;
        long savePeakTotalTime = 0;
        long updateSpectraTotalTime = 0;
        long numClusters = 0;

        progress = 0F;
        final ChromatographyType[] values = ChromatographyType.values();
        final float step = 1F / values.length;

        Map<String, Map<String, Integer>> highResDbCountMaps =
                distributionService.getAllDbCountMaps(MassSpectrometryType.HIGH_RESOLUTION);

        Map<String, Map<String, Integer>> lowResDbCountMaps =
                distributionService.getAllDbCountMaps(MassSpectrometryType.LOW_RESOLUTION);

        float count = 0F;
        long total = 0;

        for (int i = 0; i < values.length; i++) {
            final ChromatographyType type = values[i];
            LOGGER.info(String.format("Clustering spectra of type \"%s\"...", type));

            final List<Spectrum> spectra = MappingUtils.toList(spectrumRepository.findSpectraForClustering(type));

            final Matrix matrix = new DistanceMatrixWrapper(pageable -> spectrumMatchRepository.findByChromatographyType(type, pageable), spectra);

            if (matrix.getNumElements() == 0) {
                LOGGER.info(String.format("No matches found for spectra of type \"%s\".", type));
                progress = (i + 1) * step;
                continue;
            }

            final SparseHierarchicalClusterer clusterer = new SparseHierarchicalClusterer(
                    matrix, new org.dulab.jsparcehc.CompleteLinkage());
            try {
                clusterer.cluster(SCORE_TOLERANCE);
            } catch (final Exception e) {
                e.printStackTrace();
                throw new IllegalStateException(
                        "Something is wrong with the sparse hierarchical clustering: " + e.getMessage());
            }

            final Map<Integer, Integer> labelMap = clusterer.getLabels();

            final long[] uniqueLabels = labelMap.values()
                    .stream()
                    .mapToLong(Integer::longValue)
                    .distinct()
                    .toArray();

            total = total + uniqueLabels.length;

            LOGGER.info(String.format("Creating new clusters of type \"%s\" to the database...", type));

            for (final long label : uniqueLabels) {

                System.out.println(label);

                count += 1F;
                final Set<Long> spectrumIds = labelMap.entrySet()
                        .stream()
                        .filter(e -> e.getValue() == label)
                        .map(Map.Entry::getKey)
                        .map(index -> spectra.get(index).getId())
                        .collect(Collectors.toSet());

                if (spectrumIds.size() >= MIN_NUM_SPECTRA) {

                    numClusters += 1;

                    long time = System.currentTimeMillis();
                    final SpectrumCluster cluster = createCluster(
                            spectrumIds, highResDbCountMaps, lowResDbCountMaps);
                    createClusterTotalTime += System.currentTimeMillis() - time;

                    final Spectrum consensusSpectrum = cluster.getConsensusSpectrum();

                    //set chromatography for cluster
                    cluster.setChromatographyType(type);

                    final List<Peak> peaks = new ArrayList<>(consensusSpectrum.getPeaks());
                    final List<SpectrumProperty> properties = new ArrayList<>(consensusSpectrum.getProperties());

                    time = System.currentTimeMillis();
                    spectrumClusterRepository.save(cluster);
                    distributionRepository.saveAll(cluster.getTagDistributions());
                    saveClusterTotalTime += System.currentTimeMillis() - time;

                    time = System.currentTimeMillis();
                    spectrumRepository.savePeaksAndProperties(consensusSpectrum.getId(), peaks, properties);
                    savePeakTotalTime += System.currentTimeMillis() - time;

                    time = System.currentTimeMillis();
                    spectrumRepository.updateClusterForSpectra(cluster, spectrumIds);
                    updateSpectraTotalTime += System.currentTimeMillis() - time;
                }

                if (numClusters > 0 && numClusters % 100 == 0) {
                    LOGGER.info(String.format("Statistics for %d created clusters\n" +
                                    "Average time to create a cluster: %.3f ms\n" +
                                    "Average time to save a cluster: %.3f ms\n" +
                                    "Average time to save consensus spectrum: %.3f ms\n" +
                                    "Average time to update spectra: %.3f ms",
                            numClusters, (double) createClusterTotalTime / numClusters,
                            (double) saveClusterTotalTime / numClusters,
                            (double) savePeakTotalTime / numClusters,
                            (double) updateSpectraTotalTime / numClusters));
                }

                progress = count / total;
            }
        }

        progress = -0.1F;
    }

    private SpectrumCluster createCluster(final Set<Long> spectrumIds,
                                          Map<String, Map<String, Integer>> highResDbCountMaps,
                                          Map<String, Map<String, Integer>> lowResDbCountMaps)
            throws EmptySearchResultException {

        final SpectrumCluster cluster = new SpectrumCluster();
        final List<Spectrum> spectra = spectrumIds.stream()
                .map(this::findSpectrum)
//                .peek(s -> s.setCluster(cluster))
                .collect(Collectors.toList());

        //set size of study
        long submissionCount = spectra.stream()
                .map(Spectrum::getFile).filter(Objects::nonNull)
                .map(File::getSubmission).filter(Objects::nonNull)
                .distinct()
                .count();
        cluster.setSize((int) submissionCount);

//        cluster.setSpectra(spectra);

        // Calculate diameter
        cluster.setDiameter(spectra
                .stream()
                .flatMap(s -> s.getMatches().stream())
                .filter(m -> spectrumIds.contains(m.getMatchSpectrum().getId()))
                .mapToDouble(SpectrumMatch::getScore)
                .min()
                .orElse(0.0));

        // Calculate the significance statistics
        final DoubleSummaryStatistics significanceStats = spectra
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
        // setDiversityIndices(cluster);
        final List<TagInfo> tagInfoList = ControllerUtils.getDiversityIndices(spectra);

        if (!tagInfoList.isEmpty()) {
            Double minDiversity = Double.MAX_VALUE;
            Double maxDiversity = 0.0;
            Double avgDiversity = 0.0;
            for (final TagInfo tagInfo : tagInfoList) {
                final Double diversity = tagInfo.getDiversity();

                avgDiversity += diversity;
                if (diversity > maxDiversity) {
                    maxDiversity = diversity;
                }
                if (diversity < minDiversity) {
                    minDiversity = diversity;
                }
            }
            avgDiversity = avgDiversity / tagInfoList.size();
            cluster.setMinDiversity(minDiversity);
            cluster.setMaxDiversity(maxDiversity);
            cluster.setAveDiversity(avgDiversity);
        }

        final Spectrum consensusSpectrum = createConsensusSpectrum(spectra, MZ_TOLERANCE);
        consensusSpectrum.setCluster(cluster);
        cluster.setConsensusSpectrum(consensusSpectrum);

        //get cluster tags of unique submission
        List<SubmissionTag> tags = spectra.stream()
                .map(Spectrum::getFile).filter(Objects::nonNull)
                .map(File::getSubmission).filter(Objects::nonNull)
                .distinct()
                .flatMap(s -> s.getTags().stream())
                .collect(Collectors.toList());

        // Calculate cluster distributions
        List<TagDistribution> distributions = distributionService.calculateClusterDistributions(tags,
                consensusSpectrum.isIntegerMz() ? MassSpectrometryType.LOW_RESOLUTION : MassSpectrometryType.HIGH_RESOLUTION,
                consensusSpectrum.isIntegerMz() ? lowResDbCountMaps : highResDbCountMaps);

        distributions.forEach(d -> d.setCluster(cluster));
        cluster.setTagDistributions(distributions, true);

        return cluster;
    }

    private Spectrum findSpectrum(final long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(() -> new EmptySearchResultException(id));
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

        // Create consensus peaks
        List<Peak> consensusPeaks;
        if (checkForIntegerMzValues(spectra))
            consensusPeaks = createConsensusPeaksWithIntegerMz(spectra);
        else
            consensusPeaks = createConsensusPeaksWithFractionalMz(spectra, mzTolerance);

        // Filter peaks
        double intensityThreshold = PEAK_INTENSITY_FRACTION * consensusPeaks.stream()
                .mapToDouble(Peak::getIntensity)
                .max()
                .orElse(0.0);

        consensusPeaks = consensusPeaks.stream()
                .filter(p -> p.getIntensity() > intensityThreshold)
                .collect(Collectors.toList());

        Spectrum consensusSpectrum = new Spectrum();
        consensusSpectrum.setChromatographyType(type);
        consensusSpectrum.setConsensus(true);
        consensusSpectrum.setReference(false);
        consensusSpectrum.addProperty("Name", getName(spectra));

        consensusPeaks.forEach(p -> p.setSpectrum(consensusSpectrum));
        consensusSpectrum.setPeaks(consensusPeaks, true);

        spectra.stream()
                .map(Spectrum::getPrecursor)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .ifPresent(consensusSpectrum::setPrecursor);

        return consensusSpectrum;
    }


    private Matrix getMzDistanceMatrix(List<Spectrum> spectra, float mzTolerance) {

        MatrixImpl distanceMatrix = new MatrixImpl(mzTolerance);

        for (int i = 0; i < spectra.size(); ++i) {

            List<Peak> peaks1 = spectra.get(i).getPeaks();
            double intensityThreshold1 = PEAK_INTENSITY_FRACTION * peaks1.stream()
                    .mapToDouble(Peak::getIntensity)
                    .max()
                    .orElse(0.0);

            for (Peak peak1 : peaks1) {

                if (peak1.getIntensity() < intensityThreshold1)
                    continue;

                for (int j = i + 1; j < spectra.size(); ++j) {

                    List<Peak> peaks2 = spectra.get(j).getPeaks();
                    double intensityThreshold2 = PEAK_INTENSITY_FRACTION * peaks2.stream()
                            .mapToDouble(Peak::getIntensity)
                            .max()
                            .orElse(0.0);

                    for (Peak peak2 : peaks2) {

                        if (peak2.getIntensity() < intensityThreshold2)
                            continue;

                        distanceMatrix.add(
                                (int) peak1.getId(),
                                (int) peak2.getId(),
                                (float) Math.abs(peak1.getMz() - peak2.getMz()));
                    }
                }
            }
        }

        return distanceMatrix;
    }

    private List<Peak> createConsensusPeaksWithFractionalMz(List<Spectrum> spectra, float mzTolerance) {

        if (spectra.size() == 1) {

            // Return copy of the spectrum peaks
            List<Peak> peaks = spectra.get(0).getPeaks();

            List<Peak> newPeaks = new ArrayList<>(peaks.size());
            for (int i = 0; i < peaks.size(); ++i) {
                Peak oldPeak = peaks.get(i);
                Peak newPeak = new Peak();
                newPeak.setMz(oldPeak.getMz());
                newPeak.setIntensity(oldPeak.getIntensity());
                newPeaks.add(newPeak);
            }
            return newPeaks;
        }

        Matrix distanceMatrix = getMzDistanceMatrix(spectra, mzTolerance);

        SparseHierarchicalClusterer clusterer = new SparseHierarchicalClusterer(
                distanceMatrix, new org.dulab.jsparcehc.CompleteLinkage());
        clusterer.cluster(mzTolerance);
        Map<Integer, Integer> labels = clusterer.getLabels();

        Set<Integer> uniqueLabels = new HashSet<>(labels.values());
        final List<Peak> consensusPeaks = new ArrayList<>(uniqueLabels.size());
        List<Peak> clusterPeaks = new ArrayList<>();
        for (final int label : uniqueLabels) {

            clusterPeaks.clear();
            for (Spectrum spectrum : spectra)
                for (Peak peak : spectrum.getPeaks()) {
                    Integer l = labels.get((int) peak.getId());
                    if (l != null && label == l)
                        clusterPeaks.add(peak);
                }

            double averageMz = 0.0;
            double averageIntensity = 0.0;
            for (Peak peak : clusterPeaks) {
                averageMz += peak.getMz();
                averageIntensity += peak.getIntensity();
            }
            averageMz /= clusterPeaks.size();
            averageIntensity /= spectra.size();

            final Peak consensusPeak = new Peak();
            consensusPeak.setMz(averageMz);
            consensusPeak.setIntensity(averageIntensity);
            consensusPeaks.add(consensusPeak);
        }

        return consensusPeaks;
    }

    private List<Peak> createConsensusPeaksWithIntegerMz(List<Spectrum> spectra) {

        Map<Double, Double> totalIntensityMap = new HashMap<>();
        for (Spectrum spectrum : spectra) {
            for (Peak peak : spectrum.getPeaks()) {
                double sum = totalIntensityMap.getOrDefault(peak.getMz(), 0.0);
                totalIntensityMap.put(peak.getMz(), sum + peak.getIntensity());
            }
        }

        int numSpectra = spectra.size();
        List<Peak> peaks = new ArrayList<>(totalIntensityMap.size());
        for (Map.Entry<Double, Double> e : totalIntensityMap.entrySet()) {
            Peak peak = new Peak();
            peak.setMz(e.getKey());
            peak.setIntensity(e.getValue() / numSpectra);
            peaks.add(peak);
        }

        return peaks;
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


    private boolean checkForIntegerMzValues(List<Spectrum> spectra) {
        for (Spectrum spectrum : spectra) {
            if (!spectrum.isIntegerMz())
                return false;
        }
        return true;
    }
}
