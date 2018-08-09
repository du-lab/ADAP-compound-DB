package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class MathServiceImpl implements MathService {

    @Override
    public double diversityIndex(List<Spectrum> spectra, SubmissionCategoryType type) {

        // Map the collection of spectra to a collection of distinct files
        List<File> files = spectra.stream()
                .map(Spectrum::getFile)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (files.isEmpty()) return 0.0;

        // Fill out categoryCountMap
        Map<SubmissionCategory, AtomicInteger> categoryCountMap = new HashMap<>();

        files.stream()
                .map(File::getSubmission)
                .filter(Objects::nonNull)
                .map(submission -> submission.getCategory(type))
                .forEach(category -> categoryCountMap
                        .computeIfAbsent(category, x -> new AtomicInteger())
                        .incrementAndGet());

        // Calculate entropy
        final double entropy = categoryCountMap.values().stream()
                .mapToDouble(count -> count.doubleValue() / files.size())
                .map(p -> -p * Math.log(p))
                .sum();

        // Calculate Diversity index
        return Math.exp(entropy);
    }
}
