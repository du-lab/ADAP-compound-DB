package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.DistanceMatrixWrapper;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.site.repositories.SpectrumClusterRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.jsparcehc.CompleteSparseHierarchicalClusterer;
import org.dulab.jsparcehc.Matrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.CompleteLinkage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SpectrumClustererImpl implements SpectrumClusterer {

    private final SpectrumRepository spectrumRepository;
    private final SpectrumMatchRepository spectrumMatchRepository;
    private final SpectrumClusterRepository spectrumClusterRepository;

    @Autowired
    public SpectrumClustererImpl(SpectrumRepository spectrumRepository,
                                 SpectrumMatchRepository spectrumMatchRepository,
                                 SpectrumClusterRepository spectrumClusterRepository) {

        this.spectrumRepository = spectrumRepository;
        this.spectrumMatchRepository = spectrumMatchRepository;
        this.spectrumClusterRepository = spectrumClusterRepository;
    }

    @Transactional
    @Override
    public void removeAll() {
        spectrumClusterRepository.deleteAllEmptyClusters();
    }

    @Transactional
    @Override
    public void cluster(ChromatographyType type, int minNumSpectra, float scoreTolerance, float mzTolerance) {

        List<Spectrum> spectra = ServiceUtils.toList(spectrumRepository.findSpectraForClustering(type));

        Matrix matrix = new DistanceMatrixWrapper(
                pageable -> spectrumMatchRepository.findByChromatographyType(type, pageable),
                spectra);

        if (matrix.getNumElements() == 0)
            return;

        CompleteSparseHierarchicalClusterer clusterer = new CompleteSparseHierarchicalClusterer(matrix);
        clusterer.cluster(scoreTolerance);
        Map<Integer, Integer> labelMap = clusterer.getLabels();

        long[] uniqueLabels = labelMap.values()
                .stream()
                .mapToLong(Integer::longValue)
                .distinct()
                .toArray();

        for (long label : uniqueLabels) {

            Set<Long> spectrumIds = labelMap.entrySet()
                    .stream()
                    .filter(e -> e.getValue() == label)
                    .map(Map.Entry::getKey)
                    .map(i -> spectra.get(i).getId())
                    .collect(Collectors.toSet());

            if (spectrumIds.size() < minNumSpectra)
                continue;

            SpectrumCluster cluster = createCluster(spectrumIds, mzTolerance);
            spectrumClusterRepository.save(cluster);
        }
    }

    private SpectrumCluster createCluster(Set<Long> spectrumIds, float mzTolerance)
            throws EmptySearchResultException {

        SpectrumCluster cluster = new SpectrumCluster();

        List<Spectrum> spectra = spectrumIds.stream()
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
        DoubleSummaryStatistics significanceStats = cluster.getSpectra()
                .stream()
                .map(Spectrum::getSignificance)
                .filter(Objects::nonNull)
                .collect(Collectors.summarizingDouble(Double::doubleValue));

        if (significanceStats.getCount() > 0) {
            cluster.setAveSignificance(significanceStats.getAverage());
            cluster.setMinSignificance(significanceStats.getMin());
            cluster.setMaxSignificance(significanceStats.getMax());
        }

        Spectrum consensusSpectrum = createConsensusSpectrum(spectra, mzTolerance);
        consensusSpectrum.setCluster(cluster);
        cluster.setConsensusSpectrum(consensusSpectrum);

        return cluster;
    }

    private Spectrum findSpectrum(long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(() -> new EmptySearchResultException(id));
    }

    private Spectrum createConsensusSpectrum(List<Spectrum> spectra, float mzTolerance) {

        ChromatographyType type = spectra.stream()
                .map(Spectrum::getChromatographyType)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot determine chromatography type"));

        Peak[] peaks = spectra.stream()
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
                    .sum() / spectra.size();

            Peak consensusPeak = new Peak();
            consensusPeak.setMz(mz);
            consensusPeak.setIntensity(intensity);
            consensusPeak.setSpectrum(consensusSpectrum);

            consensusPeaks.add(consensusPeak);
        }

        consensusSpectrum.setChromatographyType(type);
        consensusSpectrum.setConsensus(true);
        consensusSpectrum.setReference(false);
        consensusSpectrum.setPeaks(consensusPeaks);
        consensusSpectrum.addProperty("Name", getName(spectra));

        return consensusSpectrum;
    }

    /**
     * Selects the most frequent name in the cluster
     *
     * @param spectra list of spectra
     * @return the most frequent name
     */
    private String getName(List<Spectrum> spectra) {

        String maxName = "";
        int maxCount = 0;
        Map<String, Integer> nameCountMap = new HashMap<>();
        for (Spectrum spectrum : spectra) {
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
