package org.dulab.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.Hit;
import org.dulab.models.entities.Spectrum;
import org.dulab.models.entities.SpectrumCluster;
import org.dulab.models.entities.SpectrumMatch;
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
    public void fillSpectrumMatchTable() {

        long startingTime = System.currentTimeMillis();

        List<Spectrum> unprocessedSpectra = ServiceUtils.toList(spectrumRepository.findAllByMatchesIsEmpty());

        AtomicInteger count = new AtomicInteger(0);
        List<SpectrumMatch> spectrumMatches = new ArrayList<>();
        unprocessedSpectra.parallelStream().forEach(querySpectrum -> {

            Iterable<Hit> hits = spectrumRepository.searchSpectra(querySpectrum,
                    new CriteriaBlock(SetOperator.AND),
                    0.01F, Integer.MAX_VALUE, 0.75F);

            for (Hit hit : hits) {
                SpectrumMatch spectrumMatch = new SpectrumMatch();
                spectrumMatch.setQuerySpectrum(querySpectrum);
                spectrumMatch.setMatchSpectrum(hit.getSpectrum());
                spectrumMatch.setScore(hit.getScore());
                spectrumMatches.add(spectrumMatch);
            }

            System.out.println(String.format("[%d/%d] Calculating scores...",
                    count.incrementAndGet(), unprocessedSpectra.size()));
        });

        spectrumMatchRepository.saveAll(spectrumMatches);

        long elapsedTime = System.currentTimeMillis() - startingTime;

        LOGGER.info(String.format("Matching scores are calculated %d milliseconds.", elapsedTime));
    }

    @Transactional
    @Override
    public void cluster(float scoreThreshold, int minNumSpectra) throws EmptySearchResultException {

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
}
