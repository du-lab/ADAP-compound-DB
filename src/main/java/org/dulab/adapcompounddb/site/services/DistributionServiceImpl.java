package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.SubmissionTag;
import org.dulab.adapcompounddb.models.entities.TagDistribution;
import org.dulab.adapcompounddb.site.repositories.DistributionRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionTagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DistributionServiceImpl {

    private  SubmissionTagRepository submissionTagRepository;
    private  DistributionRepository distributionRepository;

    public DistributionServiceImpl(SubmissionTagRepository submissionTagRepository,DistributionRepository distributionRepository) {
        this.submissionTagRepository = submissionTagRepository;
        this.distributionRepository = distributionRepository;
    }

    @Transactional
    public Iterable<SubmissionTag> findSubmissionTag() {

        // Find all the tags has been submitted
        Iterable<SubmissionTag> tags = submissionTagRepository.findAll();

        // Store all the tags in a List
        List<SubmissionTag> tagKey = new ArrayList<>();
        tags.forEach(tagKey::add);

        // Find unique keys among all tags of all spectra
        final List<String> keys = tagKey.stream()
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
            List<String> tagValues = tagKey.stream()
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

                // save distribution as Json format using Jackson ObjectMapper
            try {
                // Default constructor, which will construct the default JsonFactory as necessary, use SerializerProvider as its
                // SerializerProvider, and BeanSerializerFactory as its SerializerFactory.
                 String objectMapper = new ObjectMapper().writeValueAsString(countMap);

                // Save values and their counts to TagDistribution
                TagDistribution tagDistribution = new TagDistribution();
                tagDistribution.setTagName(key);
                tagDistribution.setTagDistribution(objectMapper);
                distributionRepository.save(tagDistribution);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null ;
    }

}
