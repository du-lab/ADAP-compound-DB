package org.dulab.adapcompounddb.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.entities.SubmissionTag;
import org.dulab.adapcompounddb.models.entities.TagDistribution;
import org.dulab.adapcompounddb.site.repositories.DistributionRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionTagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DistributionServiceImpl implements DistributionService {

    private  SubmissionTagRepository submissionTagRepository;
    private  DistributionRepository distributionRepository;
    private static final Logger LOGGER = LogManager.getLogger(DistributionService.class);

    public DistributionServiceImpl(SubmissionTagRepository submissionTagRepository,DistributionRepository distributionRepository) {
        this.submissionTagRepository = submissionTagRepository;
        this.distributionRepository = distributionRepository;
    }

    @Transactional(propagation= Propagation.REQUIRES_NEW)
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
    public void calculateAllDistributions() {

        // Find all the tags has been submitted
        Iterable<SubmissionTag> tags = submissionTagRepository.findAll();

        // Store all the tags in a List
        List<SubmissionTag> tagList = new ArrayList<>();
        tags.forEach(tagList::add);

        // Find unique keys among all tags of all spectra
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

                // Save values and their counts to TagDistribution
                TagDistribution tagDistribution = new TagDistribution();
                tagDistribution.setTagDistributionMap(countMap);
                tagDistribution.setTagKey(key);
                distributionRepository.save(tagDistribution);

            }
        }
    }
