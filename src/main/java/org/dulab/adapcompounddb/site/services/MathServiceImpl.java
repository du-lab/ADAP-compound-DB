package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Service
public class MathServiceImpl implements MathService {

    @Override
    public double diversityIndex(List<SubmissionCategory> categories) {

        // Count the number of entries of each category
        Map<Optional<SubmissionCategory>, Long> categoryCountMap = categories
                .stream()
                .collect(groupingBy(Optional::ofNullable, counting()));

        // Calculate entropy
        final double entropy = categoryCountMap.values()
                .stream()
                .mapToDouble(count -> count.doubleValue() / categories.size())
                .map(p -> -p * Math.log(p))
                .sum();

        // Calculate the diversity index
        return Math.exp(entropy);
    }
}
