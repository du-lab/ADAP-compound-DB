package org.dulab.adapcompounddb.site.services.utils;

import org.apache.commons.math3.special.Gamma;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class IsotopicDistributionUtils {

    private static final Logger LOGGER = LogManager.getLogger(IsotopicDistributionUtils.class);

    private static final double ZERO = 1e-300;

    public static final Map<String, SortedMap<Double, Double>> ISOTOPE_TABLE = new HashMap<>();

    //TODO Add "P"
    static {
//        ISOTOPE_TABLE.put("H", new TreeMap<>(Map.of(1.0078246, 99.9855, 2.0078246, 0.0145)));
////        ISOTOPE_TABLE.put("C", new TreeMap<>(Map.of(12.0, 98.94, 13.0, 1.06)));
//        ISOTOPE_TABLE.put("C", new TreeMap<>(Map.of(12.0, 100.0, 13.00336, 1.0816)));
//        ISOTOPE_TABLE.put("N", new TreeMap<>(Map.of(14.003074, 99.6205, 15.003074, 0.3795)));
//        ISOTOPE_TABLE.put("O", new TreeMap<>(Map.of(15.994915, 99.757, 16.994915, 0.03835,
//                17.994915, 0.2045)));
//        ISOTOPE_TABLE.put("S", new TreeMap<>(Map.of(31.972072, 94.85, 32.972072, 0.763,
//                33.972072, 4.365, 34.972072, ZERO, 35.972072, 0.0158)));
//        ISOTOPE_TABLE.put("Cl", new TreeMap<>(Map.of(34.968853, 75.8, 35.968853, ZERO,
//                36.968853, 24.2)));
//        ISOTOPE_TABLE.put("Br", new TreeMap<>(Map.of(78.918336, 50.65, 79.918336, ZERO,
//                80.918336, 49.35)));
//        ISOTOPE_TABLE.put("Si", new TreeMap<>(Map.of(27.976928, 92.2545, 28.976928, 4.672,
//                29.976928, 3.0735)));
        ISOTOPE_TABLE.put("H", new TreeMap<>(Map.of(1.0, 99.9855, 2.0, 0.0145)));
//        ISOTOPE_TABLE.put("C", new TreeMap<>(Map.of(12.0, 98.94, 13.0, 1.06)));
        ISOTOPE_TABLE.put("C", new TreeMap<>(Map.of(12.0, 100.0, 13.0, 1.0816)));
        ISOTOPE_TABLE.put("N", new TreeMap<>(Map.of(14.0, 99.6205, 15.0, 0.3795)));
        ISOTOPE_TABLE.put("O", new TreeMap<>(Map.of(16.0, 99.757, 17.0, 0.03835,18.0, 0.2045)));
        ISOTOPE_TABLE.put("S", new TreeMap<>(Map.of(32.0, 94.85, 33.0, 0.763,
                34.0, 4.365, 35.0, ZERO, 36.0, 0.0158)));
        ISOTOPE_TABLE.put("Cl", new TreeMap<>(Map.of(35.0, 75.8, 36.0, ZERO, 37.0, 24.2)));
        ISOTOPE_TABLE.put("Br", new TreeMap<>(Map.of(79.0, 50.65, 80.0, ZERO,81.0, 49.35)));
        ISOTOPE_TABLE.put("Si", new TreeMap<>(Map.of(28.0, 92.2545, 29.0, 4.672,30.0, 3.0735)));
        ISOTOPE_TABLE.put("P", new TreeMap<>(Map.of(31.0, 100.0)));
    }

    /**
     * Returns all combinations of k integers whose sum is equal to n
     *
     * @param n number of atoms
     * @param k number of isotopes
     * @return combinations
     */
    public static List<int[]> getCombinations(int n, int k) {
        List<int[]> combinations = new ArrayList<>();

        int[] combination = new int[k];
        combination[0] = n;

        int k1 = 0;
        int k2 = 1;
        while (combination[k - 1] < n) {
            combinations.add(combination.clone());
            if (k1 == k - 1) {
                while (k1 >= 0) {
                    combination[k1] = 0;
                    --k1;
                    if (combination[k1] > 0)
                        break;
                }
            }
            combination[k1] -= 1;
            k2 = k1 + 1;
            combination[k2] = n - Arrays.stream(combination, 0, k1 + 1).sum();
            k1 = k2;
        }
        combinations.add(combination.clone());
        return combinations;
    }

    /**
     * Returns isotopic distribution of the same n atoms
     *
     * @param distribution distribution of a single atom
     * @param n            number of atoms
     * @return isotopic distribution
     */
    public static SortedMap<Double, Double> calculateDistributionOfAtoms(
            SortedMap<Double, Double> distribution, int n) {

        int k = distribution.size();
        List<int[]> combinations = getCombinations(n, k);

        SortedMap<Double, Double> outputDistribution = new TreeMap<>();
        double lnFactorialN = Gamma.logGamma(n + 1);
        for (int[] combination : combinations) {
            double lnCoefficient = lnFactorialN;
            double mass = 0.0;
            Iterator<Map.Entry<Double, Double>> it = distribution.entrySet().iterator();
            for (int i = 0; i < combination.length && it.hasNext(); ++i) {
                Map.Entry<Double, Double> entry = it.next();
                lnCoefficient += combination[i] * Math.log(entry.getValue()) - Gamma.logGamma(combination[i] + 1);
                mass += entry.getKey() * combination[i];
            }
            outputDistribution.merge(mass, Math.exp(lnCoefficient), Double::sum);
        }

        scale(outputDistribution);

        return outputDistribution;
    }

    /**
     * Returns isotopic distribution of two different atoms
     * @param distribution1 distribution of the first atom
     * @param distribution2 distribution of the second atom
     * @return isotopic distribution
     */
    public static SortedMap<Double, Double> calculateDistributionOfTwoAtoms(
            SortedMap<Double, Double> distribution1, SortedMap<Double, Double> distribution2) {

        if (distribution1.isEmpty()) return distribution2;
        if (distribution2.isEmpty()) return distribution1;

        SortedMap<Double, Double> outputDistribution = new TreeMap<>();
        for (Map.Entry<Double, Double> entry1 : distribution1.entrySet()) {
            for (Map.Entry<Double, Double> entry2 : distribution2.entrySet()) {
                double mass = entry1.getKey() + entry2.getKey();
                outputDistribution.merge(mass, entry1.getValue() * entry2.getValue(), Double::sum);
            }
        }

        scale(outputDistribution);

        return outputDistribution;
    }

    /**
     * Returns the isotopic distribution for the given formula
     * @param formula molecular formula
     * @return isotopic distribution
     */
    public static SortedMap<Double, Double> calculateDistribution(String formula) {
        Map<String, Integer> atoms = readFormula(formula);
        SortedMap<Double, Double> distribution = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : atoms.entrySet()) {
            SortedMap<Double, Double> atomDistribution = ISOTOPE_TABLE.get(entry.getKey());
            if (atomDistribution == null) {
                LOGGER.warn("Unknown distribution of atom " + entry.getKey());
                return Collections.emptySortedMap();
            }

            distribution = calculateDistributionOfTwoAtoms(
                    distribution, calculateDistributionOfAtoms(atomDistribution, entry.getValue()));
        }
        return distribution;
    }

    public static double[] calculateDistributionAsArray(String formula) {
        SortedMap<Double, Double> distribution = calculateDistribution(formula);
        return distribution.values().stream().mapToDouble(Double::doubleValue).toArray();
    }

    public static Map<String, Integer> readFormula(String formula) {
        Map<String, Integer> atoms = new HashMap<>();

        StringBuilder atomName = new StringBuilder();
        StringBuilder atomCount = new StringBuilder();
        for (char c : formula.toCharArray()) {
            if ('a' <= c && c <= 'z') {
                atomName.append(c);
            } else if ('A' <= c && c <= 'Z') {
                if (atomName.length() > 0) {
                    atoms.put(atomName.toString(), (atomCount.length() == 0) ? 1 : Integer.parseInt(atomCount.toString()));
                    atomName = new StringBuilder();
                    atomCount = new StringBuilder();
                }
                atomName.append(c);
            } else if ('0' < c && c < '9') {
                atomCount.append(c);
            }
        }

        if (atomName.length() > 0)
            atoms.put(atomName.toString(), (atomCount.length() == 0) ? 1 : Integer.parseInt(atomCount.toString()));

        return atoms;
    }


    private static void scale(Map<Double, Double> distribution) {
        distribution.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .ifPresent(max -> distribution.replaceAll((key, value) -> value * 100 / max));
    }
}
