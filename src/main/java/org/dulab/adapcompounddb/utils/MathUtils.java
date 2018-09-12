package org.dulab.adapcompounddb.utils;

import org.dulab.adapcompounddb.models.entities.SubmissionCategory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class MathUtils {

    public static double diversityIndex(List<SubmissionCategory> categories) {

        // Count the number of entries of each category
        Map<Optional<SubmissionCategory>, Long> categoryCountMap = categories
                .stream()
                .collect(groupingBy(Optional::ofNullable, counting()));

        // Calculate entropy
        final double entropy = categoryCountMap.values()
                .stream()
                .mapToDouble(count -> count.doubleValue() / categories.size())
                .map(p -> -p * java.lang.Math.log(p))
                .sum();

        // Calculate the diversity index
        return java.lang.Math.exp(entropy);
    }
}
