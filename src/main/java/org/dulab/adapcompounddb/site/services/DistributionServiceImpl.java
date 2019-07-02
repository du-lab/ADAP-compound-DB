package org.dulab.adapcompounddb.site.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
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
    public DistributionServiceImpl(@Lazy final SubmissionTagRepository submissionTagRepository, @Lazy final DistributionRepository distributionRepository, @Lazy final SpectrumClusterRepository spectrumClusterRepository, @Lazy final DistributionService distributionService) {
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
    public JSONObject integrationAllTagsAndClusterDistribution(SpectrumCluster cluster) {

        final List<TagDistribution> clusterDistributions = cluster.getTagDistributions();

        // get cluster tag distributions count map
        final List<Map<String, Integer>> clusterTagDistributionsMap = new ArrayList<>();

        for (TagDistribution x : clusterDistributions) {
            clusterTagDistributionsMap.add(x.getTagDistributionMap());
        }

        // get all tag distributions count map
        final List<String> allTagDistributions = distributionService.getClusterTagDistributions(cluster);

        final List<Map<String, Integer>> allTagDistributionsMap = new ArrayList<>();

        for (String at : allTagDistributions) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                allTagDistributionsMap.add((Map<String, Integer>) mapper.readValue(at, Map.class));
            } catch (IOException e) {
                throw new IllegalStateException("It cannot be converted from Json-String to map!");
            }
        }

        //put tag distributions with the same tagKey and Tag values
        JSONObject integrationDistributionObject = new JSONObject();

        // iterate all cluster tag distributions map
        for (int i = 0; i < clusterTagDistributionsMap.size(); i++) {

            TagDistribution tagDistribution = new TagDistribution();
            //convert map into Json array (for individual cluster tags distributions)
            JSONObject jsonObject1 = new JSONObject(tagDistribution.setTagDistributionMap(clusterTagDistributionsMap.get(i)));

            for (String key1 : jsonObject1.keySet()) {
                Integer value1 = jsonObject1.getInt(key1);

                for (int m = 0; m < allTagDistributionsMap.size(); m++) {
                    JSONObject jsonObject2 = new JSONObject(tagDistribution.setTagDistributionMap(allTagDistributionsMap.get(m)));

                    for (String key2 : jsonObject2.keySet()) {
                        Integer value2 = jsonObject2.getInt(key2);
                        if (key2.equals(key1)) {
                            Integer[] value = {value1, value2};
                            integrationDistributionObject.put(key1, value);
                        }
                    }
                }
            }
        }
        return integrationDistributionObject;
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
}
