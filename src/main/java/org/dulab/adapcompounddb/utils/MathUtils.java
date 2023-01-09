package org.dulab.adapcompounddb.utils;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.springframework.stereotype.Repository;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MathUtils {

    public static <T> double diversityIndex(final List<T> categories) {

        // Count the number of entries of each category
        final Map<Optional<T>, Long> categoryCountMap = categories
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

    public static void main (String[] args){
        SpectrumMatch emptyMatch = new SpectrumMatch();
        emptyMatch.setQuerySpectrum(new Spectrum());
//        emptyMatch.setQuerySpectrum(querySpectrum);
//        spectrumMatchRepository.save(emptyMatch);

    }

}
