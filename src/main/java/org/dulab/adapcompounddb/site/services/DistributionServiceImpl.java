package org.dulab.adapcompounddb.site.services;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.repositories.DistributionRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumClusterRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionTagRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DistributionServiceImpl implements DistributionService {

    private SubmissionTagRepository submissionTagRepository;
    private DistributionRepository distributionRepository;
    private SpectrumClusterRepository spectrumClusterRepository;
    private final DistributionService distributionService;
    private static final Logger LOGGER = LogManager.getLogger(DistributionService.class);

    @Autowired
    public DistributionServiceImpl(@Lazy final SubmissionTagRepository submissionTagRepository,
                                   @Lazy final DistributionRepository distributionRepository,
                                   @Lazy final SpectrumClusterRepository spectrumClusterRepository,
                                   @Lazy final DistributionService distributionService) {
        this.submissionTagRepository = submissionTagRepository;
        this.distributionRepository = distributionRepository;
        this.spectrumClusterRepository = spectrumClusterRepository;
        this.distributionService = distributionService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void removeAll() {
        LOGGER.info("Deleting old tagKey...");
        try {
            distributionRepository.deleteAll();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Deleting old tagKey is completed");
    }

    @Transactional
    @Override
    public List<TagDistribution> getAllDistributions() {
        return ServiceUtils.toList(distributionRepository.findAll());
    }

    @Transactional
    @Override
    public List<TagDistribution> getAllClusterIdNullDistributions() {
        return ServiceUtils.toList(distributionRepository.getAllByClusterIdIsNull());
    }

    @Transactional
    @Override
    public TagDistribution getDistribution(final long id) {
        return distributionRepository.findById(id)
                .orElseThrow(() -> new EmptySearchResultException(id));
    }

    @Transactional
    @Override
    public List<String> getClusterTagDistributions(final SpectrumCluster cluster) {

        final List<TagDistribution> clusterTagDistributions = cluster.getTagDistributions();

        final List<String> tagKeys = clusterTagDistributions.stream()
                .map(s -> s.getTagKey())
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<String> allTagDistributions = new ArrayList<>();

        for (String tagKey : tagKeys) {
            String tagDistribution = distributionRepository.findTagDistributionByTagKey(tagKey);
            allTagDistributions.add(tagDistribution);
        }
        return allTagDistributions;
    }

    @Transactional
    @Override
    public void calculateAllDistributions() {

        // Find all the tags has been submitted
        List<SubmissionTag> tags = ServiceUtils.toList(submissionTagRepository.findAll());

        findAllTags(tags, null);
    }

    @Transactional
    @Override
    public void calculateClusterDistributions() {

        List<SpectrumCluster> clusters = ServiceUtils.toList(spectrumClusterRepository.getAllClusters());

        for (SpectrumCluster cluster : clusters) {

            List<Spectrum> spectra = cluster.getSpectra();

            //get cluster tags of unique submission
            List<SubmissionTag> clusterTags = spectra.stream()
                    .map(Spectrum::getFile).filter(Objects::nonNull)
                    .map(File::getSubmission).filter(Objects::nonNull)
                    .distinct()
                    .flatMap(s -> s.getTags().stream())
                    .collect(Collectors.toList());

            // calculate tags unique submission distribution and save to the TagDistribution table
            findAllTags(clusterTags, cluster);
        }
    }


    @Transactional
    @Override
    public Map<String, JSONObject> integrateDbAndClusterDistributions(SpectrumCluster cluster) {

        final List<TagDistribution> clusterDistributions = cluster.getTagDistributions();

        // get all tag distributions count map
        final List<TagDistribution> allTagDistributions = distributionService.getAllClusterIdNullDistributions();

        Map<String, JSONObject> integrationDistributionsMap = new HashMap<>();
        for (TagDistribution clusterTagDistribution : clusterDistributions) {
            String clusterTagKey = clusterTagDistribution.getTagKey();
            for (TagDistribution dbTagDistribution : allTagDistributions) {
                String dbTagKey = dbTagDistribution.getTagKey();
                if (dbTagKey.equalsIgnoreCase(clusterTagKey)) {
                    integrationDistributionsMap.put(
                            clusterTagKey,
                            getIntegratedDistribution(clusterTagDistribution, dbTagDistribution));
                    break;
                }
            }
        }
        return integrationDistributionsMap;
    }

    private JSONObject getIntegratedDistribution(TagDistribution clusterTagDistribution,
                                                 TagDistribution dbTagDistribution) {

        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<String, Integer> e : dbTagDistribution.getTagDistributionMap().entrySet()) {
            String key = e.getKey();
            Integer dbValue = e.getValue();
            Integer clusterValue = clusterTagDistribution.getTagDistributionMap().get(key);
            Map<String, Integer> values = new HashMap<>();
            values.put("alldb", dbValue);
            if (clusterValue == null) {
                values.put("cluster", 0);
            } else {
                values.put("cluster", clusterValue);
            }
            jsonObject.put(key, values);
        }
        return jsonObject;
    }

    private void findAllTags(List<SubmissionTag> tagList, SpectrumCluster cluster) {

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
        // For each key, find its values and their count
        for (String key : keys) {
            Map<String, Integer> countMap = new HashMap<>();
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

            for (String value : tagValues)
                countMap.compute(value, (k, v) -> (v == null) ? 1 : v + 1);

            //store tagDistributions
            TagDistribution tagDistribution = new TagDistribution();
            tagDistribution.setTagDistributionMap(countMap);
            tagDistribution.setCluster(cluster);
            tagDistribution.setTagKey(key);
            distributionRepository.save(tagDistribution);
        }
    }

    // calculate each cluster's pValue
    @Transactional
    @Override
    public Map<String, Double> calculateClusterPvalue(SpectrumCluster cluster) {

        List<TagDistribution> clusterDistributions = cluster.getTagDistributions();

        // get all tag distributions count map
        final List<TagDistribution> allTagDistributions = distributionService.getAllClusterIdNullDistributions();
        Map<String, Double> chiSquareStatisticMap = new HashMap<>();
        for (TagDistribution clusterTagDistribution : clusterDistributions) {
            //TODO: rename k to something meaningful
            int freedomDegrees = 0;
            Double chiSquareStatistics = 0.0;
            String clusterTagKey = clusterTagDistribution.getTagKey();
            for (TagDistribution dbTagDistribution : allTagDistributions) {
                String dbTagKey = dbTagDistribution.getTagKey();
                if (dbTagKey.equalsIgnoreCase(clusterTagKey)) {
                    for (Map.Entry<String, Integer> x : clusterTagDistribution.getTagDistributionMap().entrySet()) {
                        Integer m = x.getValue();
                        for (Map.Entry<String, Integer> y : dbTagDistribution.getTagDistributionMap().entrySet()) {
                            //TODO: You can replace x.getKey().equalsIgnoreCase(y.getKey()) with clusterTagDistribution.getTagDistributionMap().get(y.getKey())
                            // and remove the loop for "for (Map.Entry<String, Integer> x : clusterTagDistribution.getTagDistributionMap().entrySet())"
                            if (m != null && x.getKey().equalsIgnoreCase(y.getKey())) {
                                double n = m;
                                freedomDegrees++;
                                Double z = (n - y.getValue()) * (n - y.getValue()) / (y.getValue());
                                chiSquareStatistics = chiSquareStatistics + z;
                            } else {
                                //TODO: Do we want to raise an exception here? I don't understand the meaning of this.
                                System.out.println(chiSquareStatistics);
                            }
                        }
                    }
                }
            }
            if (freedomDegrees > 1) {
                chiSquareStatisticMap.put(clusterTagKey, 1 - new ChiSquaredDistribution(freedomDegrees - 1).cumulativeProbability(chiSquareStatistics));
            } else {
                chiSquareStatisticMap.put(clusterTagKey, 1.0);
            }
        }
        return chiSquareStatisticMap;
    }

    // calculate all clusters' pValue
    @Transactional
    @Override
    public void calculateAllClustersPvalue() {
        List<SpectrumCluster> clusters = ServiceUtils.toList(spectrumClusterRepository.getAllClusters());

        for (SpectrumCluster cluster : clusters) {

            List<TagDistribution> tagDistribution = cluster.getTagDistributions();

            for (TagDistribution t : tagDistribution) {

                String key = t.getTagKey();

                for (Map.Entry<String, Double> x : calculateClusterPvalue(cluster).entrySet()) {

                    if (x.getKey().equalsIgnoreCase(t.getTagKey())) {

                        distributionRepository.findClusterTagDistributionByTagKey(key,cluster.getId()).setPValue(x.getValue());
                    }
                }
            }
        }
    }

    public Double getClusterPvalue(long id){
        Double pvalue = distributionRepository.getClusterPvalue(id);
        return pvalue;
    }
}
