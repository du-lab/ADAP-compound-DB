package org.dulab.adapcompounddb.site.services.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class MappingUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(MappingUtils.class);

    public static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    public static <E> Map<Long, E> toMap(Iterable<Object[]> iterable) {
        Map<Long, E> map = new HashMap<>();
        iterable.forEach(it -> map.put((Long) it[0], (E) it[1]));
        return map;
    }

    public static <E> Map<Long, List<E>> toMapOfLists(Iterable<Object[]> iterable) {
        Map<Long, List<E>> map = new HashMap<>();
        for (Object[] it : iterable) {
            Long key = (Long) it[0];
            E value = (E) it[1];
            map.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(value);
        }
        return map;
    }

    public static <E> Map<BigInteger, List<E>> toMapBigIntegerOfLists(Iterable<Object[]> iterable) {
        Map<BigInteger, List<E>> map = new HashMap<>();
        for (Object[] it : iterable) {
            BigInteger key = (BigInteger) it[0];
            E value = (E) it[1];
            map.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(value);
        }
        return map;
    }

    public static <E> List<E> randomSubList(List<E> list, int n) {
        Collections.shuffle(list, new Random(0));
        return list.size() > n ? list.subList(0, n) : list;
    }

    public static Double parseDouble(String string) {
        if (string == null || string.isEmpty())
            return null;
        try {
            return Double.parseDouble(string.replaceAll(",", "."));
        } catch (NullPointerException | NumberFormatException e) {
            LOGGER.warn("Cannot convert to a number: " + (string != null ? string : "null"));
            return null;
        }
    }

    public static SearchResultDTO mapSpectrumMatchToSpectrumClusterView(
            SpectrumMatch match, Integer matchIndex, String species, String source, String disease) {

        SearchResultDTO searchResult = new SearchResultDTO(match, matchIndex);

        Spectrum matchSpectrum = match.getMatchSpectrum();
        if (matchSpectrum != null) {
            SpectrumCluster cluster = matchSpectrum.getCluster();
            if (cluster != null) {
                searchResult.setClusterId(cluster.getId());

                List<Spectrum> spectra = cluster.getSpectra().stream()
                        .filter(spectrum -> spectrum.getFile() != null)
                        .filter(spectrum -> spectrum.getFile().getSubmission() != null)
                        .collect(Collectors.toList());

                if (species != null && !species.equalsIgnoreCase("all"))
                    spectra = spectra.stream()
                            .filter(spectrum -> spectrum.getFile().getSubmission().getTags().stream()
                                    .anyMatch(tag -> tag.getTagKey().equalsIgnoreCase("species (common)")
                                            && tag.getTagValue().equalsIgnoreCase(species)))
                            .collect(Collectors.toList());

                if (source != null && !source.equalsIgnoreCase("all"))
                    spectra = spectra.stream()
                            .filter(spectrum -> spectrum.getFile().getSubmission().getTags().stream()
                                    .anyMatch(tag -> tag.getTagKey().equalsIgnoreCase("sample source")
                                            && tag.getTagValue().equalsIgnoreCase(source)))
                            .collect(Collectors.toList());

                if (disease != null && !disease.equalsIgnoreCase("all"))
                    spectra = spectra.stream()
                            .filter(spectrum -> spectrum.getFile().getSubmission().getTags().stream()
                                    .anyMatch(tag -> tag.getTagKey().equalsIgnoreCase("disease")
                                            && tag.getTagValue().equalsIgnoreCase(disease)))
                            .collect(Collectors.toList());

                searchResult.setSize((int) spectra.stream()
                        .map(Spectrum::getFile).filter(Objects::nonNull)
                        .map(File::getSubmission).filter(Objects::nonNull)
                        .distinct().count());

                spectra.stream()
                        .map(Spectrum::getSignificance).filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .min()
                        .ifPresent(searchResult::setMinSignificance);

                spectra.stream()
                        .map(Spectrum::getSignificance).filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .max()
                        .ifPresent(searchResult::setMaxSignificance);

                spectra.stream()
                        .map(Spectrum::getSignificance).filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .ifPresent(searchResult::setAveSignificance);
            }
        }

        return searchResult;
    }
}
