package org.dulab.adapcompounddb.site.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.repositories.DistributionRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumClusterRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private DbAndClusterValuePair dbAndClusterValuePair;

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
    public List<TagDistribution> getClusterDistributions(long clusterId) {
        return ServiceUtils.toList(distributionRepository.findClusterTagDistributionsByClusterId(clusterId));
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
    public void calculateAllDistributions() throws IOException {

        // Find all the tags has been submitted
        List<SubmissionTag> tags = ServiceUtils.toList(submissionTagRepository.findAll());

        findAllTags(tags, null);

    }

    @Transactional
    @Override
    public void calculateClusterDistributions() throws IOException {

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

    //TODO: rewrite to use DbAndClusterValuePair
    private void findAllTags(List<SubmissionTag> tagList, SpectrumCluster cluster) throws IOException {

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

            if (cluster == null) {
                Map<String, DbAndClusterValuePair> countPairMap = new HashMap<>();
                for (Map.Entry<String, Integer> e : countMap.entrySet()) {
                    countPairMap.put(e.getKey(), new DbAndClusterValuePair(e.getValue(), 0));
                }
                //store tagDistributions
                TagDistribution tagDistribution = new TagDistribution();
                tagDistribution.setTagDistributionMap(countPairMap);
                tagDistribution.setCluster(cluster);
                tagDistribution.setTagKey(key);
                distributionRepository.save(tagDistribution);
            } else {
                Map<String, DbAndClusterValuePair> countPairMap = new HashMap<>();
                for (Map.Entry<String, Integer> e : countMap.entrySet()) {

                    ObjectMapper mapper = new ObjectMapper();

                    Map<String, Map> map1 = mapper.readValue(distributionRepository.findTagDistributionByTagKey(key), Map.class);


                    for (Map.Entry<String, Map> m : map1.entrySet()) {
                        Map<String, Integer> valuePair = m.getValue();
                        Integer dbValue = valuePair.get("dbValue");
                        countPairMap.put(e.getKey(), new DbAndClusterValuePair(dbValue, e.getValue()));
                    }
                }
                //store tagDistributions
                TagDistribution tagDistribution = new TagDistribution();
                tagDistribution.setTagDistributionMap(countPairMap);
                tagDistribution.setCluster(cluster);
                tagDistribution.setTagKey(key);
                distributionRepository.save(tagDistribution);
            }
        }
    }

    // calculate all clusters' pValue
    @Transactional
    @Override
    public void calculateAllClustersPvalue() {
        List<SpectrumCluster> clusters = ServiceUtils.toList(spectrumClusterRepository.getAllClusters());
        for (SpectrumCluster cluster : clusters) {
            List<TagDistribution> clusterDistributions = cluster.getTagDistributions();
            for (TagDistribution t : clusterDistributions) {
                String key = t.getTagKey();
                int freedomDegrees = 0;
                Double chiSquareStatistics = 0.0;

                for (Map.Entry<String, DbAndClusterValuePair> e : t.getTagDistributionMap().entrySet()) {
                    int clusterValue = e.getValue().getClusterValue();
                    int alldbValue = e.getValue().getDbValue();

                    Double c = new Double(clusterValue);
                    Double a = new Double(alldbValue);
                    // calculate chi-squared statistics
                    Double sum = (c - a) * (c - a) / (a);
                    chiSquareStatistics = chiSquareStatistics + sum;
                    freedomDegrees++;
                }
                if (freedomDegrees > 1) {
                    Double pValue = 1 - new ChiSquaredDistribution(freedomDegrees - 1)
                            .cumulativeProbability(chiSquareStatistics);
                    distributionRepository.findClusterTagDistributionByTagKey(key, cluster.getId()).setPValue(pValue);
                } else {
                    Double pValue = 1.0;
                    distributionRepository.findClusterTagDistributionByTagKey(key, cluster.getId()).setPValue(pValue);
                }
            }

        }

    }


}
