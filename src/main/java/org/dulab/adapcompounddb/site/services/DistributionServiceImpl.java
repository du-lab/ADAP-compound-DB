package org.dulab.adapcompounddb.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.enums.MassSpectrometryType;
import org.dulab.adapcompounddb.site.repositories.DistributionRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumClusterRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionTagRepository;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.dulab.adapcompounddb.site.services.utils.StatisticsUtils;
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

    private static final Logger LOGGER = LogManager.getLogger(DistributionService.class);

    @Autowired
    public DistributionServiceImpl(@Lazy final SubmissionTagRepository submissionTagRepository,
                                   @Lazy final DistributionRepository distributionRepository,
                                   @Lazy final SpectrumClusterRepository spectrumClusterRepository) {

        this.submissionTagRepository = submissionTagRepository;
        this.distributionRepository = distributionRepository;
        this.spectrumClusterRepository = spectrumClusterRepository;
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
        return MappingUtils.toList(distributionRepository.findAll());
    }

    @Transactional
    @Override
    public List<TagDistribution> getAllClusterIdNullDistributions() {
        return MappingUtils.toList(distributionRepository.getAllByClusterIdIsNull());
    }

    @Transactional
    @Override
    public List<TagDistribution> getClusterDistributions(long clusterId) {
        return MappingUtils.toList(distributionRepository.findClusterTagDistributionsByClusterId(clusterId));
    }

    @Transactional
    @Override
    public Double getClusterPvalue(String tagKey, long id) {
        Double pvalue = distributionRepository.getClusterPvalue(tagKey, id);
        return pvalue;
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

        LOGGER.info("Start calculating cluster distributions...");

        final List<TagDistribution> clusterTagDistributions = cluster.getTagDistributions();

        final List<String> tagKeys = clusterTagDistributions.stream()
                .map(TagDistribution::getLabel)
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<String> allTagDistributions = new ArrayList<>();

        for (String tagKey : tagKeys) {
            String tagDistribution = distributionRepository.findTagDistributionByTagKey(tagKey);
            allTagDistributions.add(tagDistribution);
        }

        LOGGER.info("Calculating cluster distributions is complete");

        return allTagDistributions;
    }

    /**
     * Calculates and saves all-db distributions to the database
     */
    @Transactional
    @Override
    public void saveAllDbDistributions() {
        // Find all tags that has been submitted
        List<SubmissionTag> tags = MappingUtils.toList(submissionTagRepository.findAll());

        List<TagDistribution> distributions = calculateAllDbDistributions(tags);

        distributionRepository.saveAll(distributions);
    }

    /**
     * Searches the database for all distributions with null-cluster and a given mass spectrometry type.
     * Then, computes a collection of countMaps from those distributions.
     *
     * @param massSpectrometryType type of mass spectrometry (high-res or low-res)
     * @return collection of count maps
     */
    @Transactional
    @Override
    public Map<String, Map<String, Integer>> getAllDbCountMaps(MassSpectrometryType massSpectrometryType) {

        Iterable<TagDistribution> tagDistributions =
                distributionRepository.findAllDbTagDistributionsByMassSpectrometryType(massSpectrometryType);

        Map<String, Map<String, Integer>> dbCountMaps = new HashMap<>();
        for (TagDistribution t : tagDistributions) {

            Map<String, Integer> dbCountMap = new HashMap<>();
            for (Map.Entry<String, DbAndClusterValuePair> m : t.getDistributionMap().entrySet()) {
                dbCountMap.put(m.getKey(), m.getValue().getDbValue());
            }

            dbCountMaps.put(t.getLabel(), dbCountMap);
        }

        return dbCountMaps;
    }

    /**
     * Calculates distributions for all tags and all studies
     *
     * @return list of distributions
     */
    @Transactional
    @Override
    public List<TagDistribution> calculateAllDbDistributions(List<SubmissionTag> tags) {

        List<TagDistribution> tagDistributionList = new ArrayList<>();
        for (MassSpectrometryType type : MassSpectrometryType.values()) {

            // Find unique keys
            final Set<String> keys = getTagKeysByType(tags, type);

            // For each key, find its values and their count
            for (String key : keys) {

                Map<String, Integer> countMap = getTagValuesByKeyAndType(tags, key, type);

                Map<String, DbAndClusterValuePair> countPairMap = new HashMap<>();
                for (Map.Entry<String, Integer> e : countMap.entrySet())
                    countPairMap.put(e.getKey(), new DbAndClusterValuePair(e.getValue(), 0));

                //store tagDistributions
                TagDistribution tagDistribution = new TagDistribution();
                tagDistribution.setDistributionMap(countPairMap);
                tagDistribution.setLabel(key);
                tagDistribution.setMassSpectrometryType(type);

                tagDistributionList.add(tagDistribution);
            }
        }

        return tagDistributionList;
    }

    /**
     * Calculates a list of distributions for a specific cluster
     * @param tags list of tags for a cluster
     * @param massSpectrometryType type of mass spectrometry
     * @param dbCountMaps study counts for the whole database
     * @return list of cluster distributions
     */
    @Transactional
    @Override
    public List<TagDistribution> calculateClusterDistributions(
            List<SubmissionTag> tags,
            MassSpectrometryType massSpectrometryType,
            Map<String, Map<String, Integer>> dbCountMaps) {

        // Find unique keys among all tags of unique submission
        final Set<String> keys = getTagKeysByType(tags, massSpectrometryType);

        // For each key, find its values and their count
        List<TagDistribution> tagDistributionList = new ArrayList<>();
        for (String key : keys) {

            Map<String, Integer> countMap = getTagValuesByKeyAndType(tags, key, massSpectrometryType);

            Map<String, DbAndClusterValuePair> clusterDistributionMap =
                    StatisticsUtils.calculateDbAndClusterDistribution(dbCountMaps.get(key), countMap);

            //store tagDistributions
            TagDistribution tagDistribution = new TagDistribution();
            tagDistribution.setDistributionMap(clusterDistributionMap);
            tagDistribution.setLabel(key);
            tagDistribution.setPValue(
                    StatisticsUtils.calculateChiSquaredStatistics(clusterDistributionMap.values()));
//            tagDistribution.setPValue(
//                    StatisticsUtils.calculateExactTestStatistics(clusterDistributionMap.values()));
            tagDistribution.setMassSpectrometryType(massSpectrometryType);

            tagDistributionList.add(tagDistribution);
        }

        return tagDistributionList;
    }


    /**
     * Returns a set of keys, where each key is the first part of SubmissionTag.Name
     *
     * @param tags list of tags
     * @param type type of mass spectrometry (high-res or low-res)
     * @return unique keys
     */
    private Set<String> getTagKeysByType(List<SubmissionTag> tags, MassSpectrometryType type) {
        return tags.stream()
                .filter(t -> t.getSubmission().getMassSpectrometryType() == type)
                .map(SubmissionTag::getTagKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Computes the count of each unique value for a given key and type of mass spectrometry
     *
     * @param tags list of tags
     * @param key  tag key
     * @param type type of mass spectrometry (high-res or low-res)
     * @return map of values and their counts
     */
    private Map<String, Integer> getTagValuesByKeyAndType(
            List<SubmissionTag> tags, String key, MassSpectrometryType type) {

        List<String> tagValues = tags.stream()
                .filter(t -> t.getSubmission().getMassSpectrometryType() == type)
                .map(SubmissionTag::getTagValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Integer> countMap = new HashMap<>();
        for (String value : tagValues)
            countMap.compute(value, (k, v) -> (v == null) ? 1 : v + 1);

        return countMap;
    }
}
