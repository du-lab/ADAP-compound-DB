package org.dulab.adapcompounddb.site.services;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
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

import java.text.DecimalFormat;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DistributionServiceImpl implements DistributionService {

    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");
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

        List<String> uniquesKeys = findUniqueKeys(tags);

        for (String key : uniquesKeys) {

            Map<String, Integer> countMap = getCountMap(tags, key);

            Map<String, DbAndClusterValuePair> countPairMap = new HashMap<>();
            for (Map.Entry<String, Integer> e : countMap.entrySet()) {
                countPairMap.put(e.getKey(), new DbAndClusterValuePair(e.getValue(), 0));
            }

            TagDistribution tagDistribution = new TagDistribution();
            tagDistribution.setTagDistributionMap(countPairMap);
            tagDistribution.setCluster(null);
            tagDistribution.setTagKey(key);
            distributionRepository.save(tagDistribution);
        }
    }

    @Transactional
    @Override
    public void calculateClusterDistributions() {

        List<TagDistribution> dbTagDistributions = ServiceUtils.toList(distributionRepository.getAllByClusterIdIsNull());
        Map<String, Map<String, DbAndClusterValuePair>> keyToCountPairMap = dbTagDistributions.stream()
                .collect(Collectors.toMap(TagDistribution::getTagKey, TagDistribution::getTagDistributionMap));

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

            List<String> uniqueKeys = findUniqueKeys(clusterTags);

            for (String key : uniqueKeys) {

                Map<String, Integer> clusterCountMap = getCountMap(clusterTags, key);
                Map<String, DbAndClusterValuePair> dbCountPairMap = keyToCountPairMap.get(key);

                Map<String, DbAndClusterValuePair> clusterCountPairMap = new HashMap<>();
                for (Map.Entry<String, DbAndClusterValuePair> e : dbCountPairMap.entrySet()) {

                    String k = e.getKey();
                    int dbValue = e.getValue().getDbValue();
                    Integer clusterValue = clusterCountMap.get(k);

                    clusterCountPairMap.put(k, new DbAndClusterValuePair(
                            dbValue, (clusterValue != null) ? clusterValue : 0));
                }

                TagDistribution tagDistribution = new TagDistribution();
                tagDistribution.setTagDistributionMap(clusterCountPairMap);
                tagDistribution.setCluster(cluster);
                tagDistribution.setTagKey(key);
                distributionRepository.save(tagDistribution);
            }
        }
    }


//    @Transactional
//    @Override
//    public Map<JSONObject, JSONObject> integrateDbAndClusterDistributions(SpectrumCluster cluster) {
//
//        final List<TagDistribution> clusterDistributions = cluster.getTagDistributions();
//
//        // get all tag distributions count map
//        final List<TagDistribution> allTagDistributions = distributionService.getAllClusterIdNullDistributions();
//
//        Map<JSONObject, JSONObject> integrationDistributionsMap = new HashMap<>();
//
//        for (TagDistribution clusterTagDistribution : clusterDistributions) {
//            String clusterTagKey = clusterTagDistribution.getTagKey();
//            final String pValue = decimalFormat.format(distributionService.getClusterPvalue(clusterTagKey,
//                    clusterTagDistribution.getCluster().getId()));
//
//            for (TagDistribution dbTagDistribution : allTagDistributions) {
//                String dbTagKey = dbTagDistribution.getTagKey();
//
//                JSONObject ClusterTagKeyAndPvalue = new JSONObject();
//
//                ClusterTagKeyAndPvalue.put(clusterTagKey, pValue);
//
//                if (dbTagKey.equalsIgnoreCase(clusterTagKey)) {
//                    integrationDistributionsMap.put(
//                            ClusterTagKeyAndPvalue,
//                            getIntegratedDistribution(clusterTagDistribution, dbTagDistribution));
//                    break;
//                }
//            }
//        }
//        return integrationDistributionsMap;
//    }

//    private JSONObject getIntegratedDistribution(TagDistribution clusterTagDistribution,
//                                                 TagDistribution dbTagDistribution) {
//
//        JSONObject jsonObject = new JSONObject();
//
//        for (Map.Entry<String, Integer> e : dbTagDistribution.getTagDistributionMap().entrySet()) {
//            String key = e.getKey();
//            Integer dbValue = e.getValue();
//            Integer clusterValue = clusterTagDistribution.getTagDistributionMap().get(key);
//            Map<String, Integer> values = new HashMap<>();
//            values.put("alldb", dbValue);
//            if (clusterValue == null) {
//                values.put("cluster", 0);
//            } else {
//                values.put("cluster", clusterValue);
//            }
//            jsonObject.put(key, values);
//        }
//        return jsonObject;
//    }

//    //TODO: rewrite to use DbAndClusterValuePair
//    private Map<String, Integer> getDistributionCountMap(List<SubmissionTag> tagList) {
//
//        // Find unique keys among all tags of unique submission
//        final List<String> keys = tagList.stream()
//                .map(t -> t.getId().getName())
//                .map(a -> {
//                    String[] values = a.split(":");
//                    if (values.length >= 2)
//                        return values[0].trim();
//                    else
//                        return null;
//                })
//                .filter(Objects::nonNull)
//                .distinct()
//                .collect(Collectors.toList());
//
//        // For each key, find its values and their count
//        for (String key : keys) {
//            Map<String, Integer> countMap = new HashMap<>();
//            List<String> tagValues = tagList.stream()
//                    .map(t -> t.getId().getName())
//                    .map(a -> {
//                        String[] values = a.split(":");
//                        if (values.length < 2 || !values[0].trim().equalsIgnoreCase(key))
//                            return null;
//                        return values[1].trim();
//                    })
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//
//            for (String value : tagValues)
//                countMap.compute(value, (k, v) -> (v == null) ? 1 : v + 1);
//
//            //store tagDistributions
//            TagDistribution tagDistribution = new TagDistribution();
//            tagDistribution.setTagDistributionMap(countMap);
//            tagDistribution.setCluster(cluster);
//            tagDistribution.setTagKey(key);
//            distributionRepository.save(tagDistribution);
//        }
//    }


    private List<String> findUniqueKeys(List<SubmissionTag> tagList) {

        return tagList.stream()
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
    }

    private Map<String, Integer> getCountMap(List<SubmissionTag> tagList, String key) {

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
        for (String value : tagValues)
            countMap.compute(value, (k, v) -> (v == null) ? 1 : v + 1);

        return countMap;
    }

    // calculate each cluster's pValue
    @Transactional
    @Override
    public Map<String, Double> calculateClusterPvalue(SpectrumCluster cluster) {

//        List<TagDistribution> clusterDistributions = cluster.getTagDistributions();
//
//        // get all tag distributions count map
//        final List<TagDistribution> allTagDistributions = distributionService.getAllClusterIdNullDistributions();
//        Map<String, Double> chiSquareStatisticMap = new HashMap<>();
//        for (TagDistribution clusterTagDistribution : clusterDistributions) {
//            int freedomDegrees = 0;
//            Double chiSquareStatistics = 0.0;
//            String clusterTagKey = clusterTagDistribution.getTagKey();
//            for (TagDistribution dbTagDistribution : allTagDistributions) {
//                String dbTagKey = dbTagDistribution.getTagKey();
//                if (dbTagKey.equalsIgnoreCase(clusterTagKey)) {
//                    for (Map.Entry<String, Integer> x : clusterTagDistribution.getTagDistributionMap().entrySet()) {
//                        Integer m = x.getValue();
//                        if (m != null) {
//                            double alldbValue = dbTagDistribution.getTagDistributionMap().get(x.getKey());
//                            double clusterValue = m;
//                            freedomDegrees++;
//                            Double sum = (clusterValue - alldbValue) * (clusterValue - alldbValue) / (alldbValue);
//                            chiSquareStatistics = chiSquareStatistics + sum;
//                        }
//                    }
//                }
//            }
//            if (freedomDegrees > 1) {
//                chiSquareStatisticMap.put(clusterTagKey, 1 - new ChiSquaredDistribution(freedomDegrees - 1)
//                        .cumulativeProbability(chiSquareStatistics));
//            } else {
//                chiSquareStatisticMap.put(clusterTagKey, 1.0);
//            }
//        }
//        return chiSquareStatisticMap;
        return null;
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

                        distributionRepository.findClusterTagDistributionByTagKey(key, cluster.getId()).setPValue(x.getValue());
                    }
                }
            }
        }
    }

    public Double getClusterPvalue(String tagKey, long id) {
        Double pvalue = distributionRepository.getClusterPvalue(tagKey, id);
        return pvalue;
    }
}
