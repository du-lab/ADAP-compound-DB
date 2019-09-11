package org.dulab.adapcompounddb.site.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.dulab.adapcompounddb.models.DistanceMatrixWrapper;
import org.dulab.adapcompounddb.models.dto.TagInfo;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.*;
import org.dulab.adapcompounddb.utils.MathUtils;
import org.dulab.jsparcehc.Matrix;
import org.dulab.jsparcehc.MatrixImpl;
import org.dulab.jsparcehc.SparseHierarchicalClusterer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
public class SpectrumClustererImpl implements SpectrumClusterer {

    private static final float MZ_TOLERANCE = 0.01F;

    private static final float SCORE_TOLERANCE = 0.25F;

    private static final int MIN_NUM_SPECTRA = 1;

    private static final double PEAK_INTENSITY_FRACTION = 0.05;

    private static final Logger LOGGER = LogManager.getLogger(SpectrumClusterer.class);

    private final SpectrumRepository spectrumRepository;
    private final SpectrumMatchRepository spectrumMatchRepository;
    private final SpectrumClusterRepository spectrumClusterRepository;
    private DistributionRepository distributionRepository;
    private SubmissionTagRepository submissionTagRepository;

    private float progress = -0.1F;

    @Autowired
    public SpectrumClustererImpl(final SubmissionTagRepository submissionTagRepository,
                                 final SpectrumRepository spectrumRepository,
                                 final SpectrumMatchRepository spectrumMatchRepository,
                                 final DistributionRepository distributionRepository,
                                 final SpectrumClusterRepository spectrumClusterRepository) {

        this.submissionTagRepository = submissionTagRepository;
        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
        this.spectrumClusterRepository = spectrumClusterRepository;
        this.distributionRepository = distributionRepository;

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
    public void cluster(/*final ChromatographyType type, final int minNumSpectra, final float scoreTolerance, final float mzTolerance*/) {

        try {

            progress = 0F;
            final ChromatographyType[] values = ChromatographyType.values();
            final float step = 1F / values.length;

            List<TagDistribution> tagDistributions = ServiceUtils.toList(distributionRepository.findAllTagDistribution());

            Map<String, TagDistribution> dbDistributionMap = new HashMap<>();
            for (TagDistribution t : tagDistributions) {
                dbDistributionMap.put(t.getTagKey(), t);
            }

            float count = 0F;
            long total = 0;

            for (int i = 0; i < values.length; i++) {
                final ChromatographyType type = values[i];
                LOGGER.info(String.format("Clustering spectra of type \"%s\"...", type));

                final List<Spectrum> spectra = ServiceUtils.toList(spectrumRepository.findSpectraForClustering(type));

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
                }
                final Map<Integer, Integer> labelMap = clusterer.getLabels();

                final long[] uniqueLabels = labelMap.values()
                        .stream()
                        .mapToLong(Integer::longValue)
                        .distinct()
                        .toArray();

                total = total + uniqueLabels.length;

                LOGGER.info(String.format("Saving new clusters of type \"%s\" to the database...", type));

                for (final long label : uniqueLabels) {

                    count += 1F;
                    final Set<Long> spectrumIds = labelMap.entrySet()
                            .stream()
                            .filter(e -> e.getValue() == label)
                            .map(Map.Entry::getKey)
                            .map(index -> spectra.get(index).getId())
                            .collect(Collectors.toSet());

                    LOGGER.info(String.format("Creating Cluster %d of size %d...", label, spectrumIds.size()));

                    if (spectrumIds.size() >= MIN_NUM_SPECTRA) {

                        final SpectrumCluster cluster = createCluster(spectrumIds, MZ_TOLERANCE, dbDistributionMap);


                        final Spectrum consensusSpectrum = cluster.getConsensusSpectrum();

                        final List<Peak> peaks = new ArrayList<>(consensusSpectrum.getPeaks());
                        final List<SpectrumProperty> properties = new ArrayList<>(consensusSpectrum.getProperties());

                        spectrumClusterRepository.save(cluster);

                        spectrumRepository.savePeaksAndProperties(consensusSpectrum.getId(), peaks, properties);
                        spectrumRepository.updateSpectraInCluster(cluster.getId(), spectrumIds);
                    }
                    progress = count / total;
                }
            }
            progress = -0.1F;
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @Override
    public void calculateAllDistributions() throws IOException {

        // Find all the tags has been submitted
        List<SubmissionTag> tags = ServiceUtils.toList(submissionTagRepository.findAll());

        findAllTags(tags, null, null);

    }

    private SpectrumCluster createCluster(final Set<Long> spectrumIds, final float mzTolerance,
                                          Map<String, TagDistribution> dbDistributionMap)
            throws EmptySearchResultException, IOException {

        LOGGER.info("Creating a cluster...");

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
//        cluster.setSize(spectra.size());

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

        LOGGER.info("Creating a consensus spectrum...");
        final Spectrum consensusSpectrum = createConsensusSpectrum(spectra, mzTolerance);
        consensusSpectrum.setCluster(cluster);
        cluster.setConsensusSpectrum(consensusSpectrum);

        LOGGER.info("Calculating distributions...");
        //calculate each cluster's Tag distribution and minimum PValue
        cluster.setMinPValue(calculateClusterDistributions(spectra, dbDistributionMap, cluster));

        return cluster;
    }

    private Double calculateClusterDistributions(List<Spectrum> spectra, Map<String, TagDistribution> dbDistributionMap, SpectrumCluster cluster) throws IOException {

        //get cluster tags of unique submission
        List<SubmissionTag> clusterTags = spectra.stream()
                .map(Spectrum::getFile).filter(Objects::nonNull)
                .map(File::getSubmission).filter(Objects::nonNull)
                .distinct()
                .flatMap(s -> s.getTags().stream())
                .collect(Collectors.toList());

        // calculate tags unique submission distribution and save to the TagDistribution table
        return findAllTags(clusterTags, dbDistributionMap, cluster);
    }

    private Double findAllTags(List<SubmissionTag> tagList, Map<String, TagDistribution> dbDistributionMaps, SpectrumCluster cluster) throws IOException {

        // Find unique keys among all tags of unique submission
        final List<String> keys = tagList.stream()
                .map(t -> t.getId().getName())
                .map(a -> {
                    String[] values = a.split(":");
                    if (values.length >= 2)
                        return values[0].trim();
                    else
                        return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<TagDistribution> tagDistributionList = new ArrayList<>();
        List<Double> clusterPvalue = new ArrayList<>();
        // For each key, find its values and their count
        for (String key : keys) {
            List<String> tagValues = tagList.stream()
                    .map(t -> t.getId().getName())
                    .map(a -> {
                        String[] values = a.split(":");
                        if (values.length < 2 || !values[0].trim().equalsIgnoreCase(key))
                            return null;
                        return values[1].trim();
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Map<String, Integer> countMap = new HashMap<>();

            for (String value : tagValues) {
                countMap.compute(value, (k, v) -> (v == null) ? 1 : v + 1);
            }

            Map<String, DbAndClusterValuePair> countPairMap = new HashMap<>();

            if (cluster == null) {
                for (Map.Entry<String, Integer> e : countMap.entrySet()) {
                    countPairMap.put(e.getKey(), new DbAndClusterValuePair(e.getValue(), 0));
                }
            } else {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, DbAndClusterValuePair> dbDistributionMap = mapper.readValue(
                        // TODO: use dbDitributionMap.get(key)
                        dbDistributionMaps.get(key).getTagDistribution(), new TypeReference<Map<String, DbAndClusterValuePair>>() {
                        });
                Map<String, Integer> dbCountMap = new HashMap<>();
                for (Map.Entry<String, DbAndClusterValuePair> m : dbDistributionMap.entrySet()) {
                    dbCountMap.put(m.getKey(), m.getValue().getDbValue());
                }
                countPairMap = ServiceUtils.calculateDbAndClusterDistribution(dbCountMap, countMap);
            }
            //store tagDistributions
            TagDistribution tagDistribution = new TagDistribution();
            tagDistribution.setTagDistributionMap(countPairMap);
            if (cluster != null) {
                tagDistribution.setCluster(cluster);
                tagDistribution.setPValue(
                        ServiceUtils.calculateExactTestStatistics(
                                tagDistribution.getTagDistributionMap().values()));
                clusterPvalue.add(tagDistribution.getPValue());
            }
            tagDistribution.setTagKey(key);

            tagDistributionList.add(tagDistribution);
        }
        distributionRepository.saveAll(tagDistributionList);

        if(cluster != null) {
            // return the minimum PValue of this cluster
            Collections.sort(clusterPvalue);
            Double minPValue = clusterPvalue.get(0);
            return minPValue;
        }
        return null;
    }


    private Spectrum findSpectrum(final long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(() -> new EmptySearchResultException(id));
    }

    private void setDiversityIndices(final SpectrumCluster cluster) {

        final List<String> tagList = new ArrayList<>();

        for (final Spectrum s : cluster.getSpectra()) {
            for (final SubmissionTag tag : s.getFile().getSubmission().getTags()) {
                tagList.add(tag.getId().getName());
            }
        }

        final Map<String, List<String>> tagMap = new HashMap<>(); // source:<src1, src2, src1, src2>

        tagList.forEach(tag -> {
            final String[] arr = tag.split(":", 2);
            if (arr.length == 2) {
                final String key = arr[0].trim();
                final String value = arr[1].trim();

                List<String> valueList = tagMap.get(key);
                if (CollectionUtils.isEmpty(valueList)) {
                    valueList = new ArrayList<>();
                    tagMap.put(key, valueList);
                }
                valueList.add(value);
            }
        });

        if (tagMap.size() == 0) {
            return;
        }

        Double minDiversity = Double.MAX_VALUE;
        Double maxDiversity = 0.0;
        Double avgDiversity = 0.0;

        for (final Entry<String, List<String>> entry : tagMap.entrySet()) {

            final double diversity = MathUtils.diversityIndex(entry.getValue());
            avgDiversity += diversity;
            if (diversity > maxDiversity) {
                maxDiversity = diversity;
            }
            if (diversity < minDiversity) {
                minDiversity = diversity;
            }
        }
        ;

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
            return peaks.stream()
                    .map(p -> {
                        Peak peak = new Peak();
                        peak.setMz(p.getMz());
                        peak.setIntensity(p.getIntensity());
                        return peak;
                    })
                    .collect(Collectors.toList());
        }

        Matrix distanceMatrix = getMzDistanceMatrix(spectra, mzTolerance);

        LOGGER.info(String.format("\tNumber of m/z distances: %d", distanceMatrix.getNumElements()));

        SparseHierarchicalClusterer clusterer = new SparseHierarchicalClusterer(
                distanceMatrix, new org.dulab.jsparcehc.CompleteLinkage());
        clusterer.cluster(mzTolerance);
        Map<Integer, Integer> labels = clusterer.getLabels();

        int[] uniqueLabels = labels.values()
                .stream()
                .mapToInt(Integer::intValue)
                .distinct()
                .toArray();

        final List<Peak> consensusPeaks = new ArrayList<>(uniqueLabels.length);

        for (final int label : uniqueLabels) {

            List<Peak> peaks = spectra.stream()
                    .flatMap(s -> s.getPeaks().stream())
                    .filter(p -> {
                        Integer l = labels.get((int) p.getId());
                        return l != null && l == label;
                    })
                    .collect(Collectors.toList());

            double mz = peaks.stream()
                    .mapToDouble(Peak::getMz)
                    .average()
                    .orElseThrow(() -> new IllegalStateException("Could not calculate average m/z value."));

            double intensity = peaks.stream()
                    .mapToDouble(Peak::getIntensity)
                    .sum() / spectra.size();

            final Peak consensusPeak = new Peak();
            consensusPeak.setMz(mz);
            consensusPeak.setIntensity(intensity);
            consensusPeaks.add(consensusPeak);
        }

        return consensusPeaks;
    }

    private List<Peak> createConsensusPeaksWithIntegerMz(List<Spectrum> spectra) {


        //TODO modify IT TO USING FOR LOOP

        // Get all distinct m/z values from all spectra
        Set<Double> mzValues = new HashSet<>();

        for (Spectrum s : spectra) {
            for (Peak p : s.getPeaks()) {
                mzValues.add(p.getMz());
            }
        }

        // Calculate average intensity for each m/z value
        double[] intensities = new double[mzValues.size()];

        List<Peak> peakList = new ArrayList<>();
        int number = 0;
        while (number < mzValues.size()) {
            double sum = 0.0;
            double mz = mzValues.iterator().next();

            for (Spectrum spectrum : spectra) {
                for (Peak peak : spectrum.getPeaks()) {
                    if ((int) peak.getMz() == mz) {
                        sum += peak.getIntensity();
                        break;
                    }
                }
            }
            intensities[number] = sum / spectra.size();
            Peak peak = new Peak();
            peak.setIntensity(intensities[number]);
            peak.setMz(mz);
            peakList.add(peak);

            number++;
        }

        return peakList;
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
        for (Spectrum spectrum : spectra)
            for (Peak peak : spectrum.getPeaks())
                if (peak.getMz() % 1 != 0)
                    return false;
        return true;
    }
}
