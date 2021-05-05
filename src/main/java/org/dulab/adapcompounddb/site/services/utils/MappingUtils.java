package org.dulab.adapcompounddb.site.services.utils;

import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class MappingUtils {

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

    public static SpectrumClusterView mapSpectrumMatchToSpectrumClusterView(
            SpectrumMatch match, String species, String source, String disease) {
        SpectrumClusterView view = new SpectrumClusterView();
        view.setUniqueId(match.getId());

        Spectrum querySpectrum = match.getQuerySpectrum();
        view.setChromatographyType(querySpectrum.getChromatographyType());

        Spectrum matchSpectrum = match.getMatchSpectrum();
        if (matchSpectrum != null) {
            view.setId(matchSpectrum.getId());
            view.setName(matchSpectrum.getName());
            view.setScore(match.getScore());

            if (querySpectrum.getMolecularWeight() != null && matchSpectrum.getMolecularWeight() != null) {
                double d = Math.abs(querySpectrum.getMolecularWeight() - matchSpectrum.getMolecularWeight());
                view.setMassError(d);
                view.setMassErrorPPM(1E6 * d / matchSpectrum.getMolecularWeight());
            }

            if (querySpectrum.getRetentionTime() != null && matchSpectrum.getRetentionTime() != null) {
                view.setRetTimeError(Math.abs(querySpectrum.getRetentionTime() - matchSpectrum.getRetentionTime()));
            }

            if (matchSpectrum.isConsensus()) {
                SpectrumCluster cluster = matchSpectrum.getCluster();
                view.setClusterId(cluster.getId());

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

                view.setSize((int) spectra.stream()
                        .map(Spectrum::getFile).filter(Objects::nonNull)
                        .map(File::getSubmission).filter(Objects::nonNull)
                        .distinct().count());

                spectra.stream()
                        .map(Spectrum::getSignificance).filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .min()
                        .ifPresent(view::setMinimumSignificance);

                spectra.stream()
                        .map(Spectrum::getSignificance).filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .max()
                        .ifPresent(view::setMaximumSignificance);

                spectra.stream()
                        .map(Spectrum::getSignificance).filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .ifPresent(view::setAverageSignificance);
            }
        }

        return view;
    }
}
