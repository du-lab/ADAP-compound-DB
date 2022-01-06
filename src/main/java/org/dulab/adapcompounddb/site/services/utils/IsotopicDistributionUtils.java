package org.dulab.adapcompounddb.site.services.utils;

import com.sun.xml.fastinfoset.algorithm.DoubleEncodingAlgorithm;
import org.apache.commons.math3.special.Gamma;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.print.attribute.standard.MediaSize;
import java.util.*;
import java.util.stream.Collectors;

public class IsotopicDistributionUtils {

    private static final Logger LOGGER = LogManager.getLogger(IsotopicDistributionUtils.class);

    private static final double MIN_INTENSITY = 0.01;
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
        ISOTOPE_TABLE.put("Al", new TreeMap<>(Map.of(27.0, 100.0)));
        ISOTOPE_TABLE.put("As", new TreeMap<>(Map.of(75.0, 100.0)));
        ISOTOPE_TABLE.put("Au", new TreeMap<>(Map.of(197.0, 100.0)));
        ISOTOPE_TABLE.put("B", new TreeMap<>(Map.of(10.0, 24.6883, 11.0, 100.0)));
        ISOTOPE_TABLE.put("Bi", new TreeMap<>(Map.of(209.0, 100.0)));
        ISOTOPE_TABLE.put("Br", new TreeMap<>(Map.of(79.0, 50.65, 80.0, ZERO,81.0, 49.35)));
        ISOTOPE_TABLE.put("C", new TreeMap<>(Map.of(12.0, 100.0, 13.0, 1.0816)));
        ISOTOPE_TABLE.put("Ca", new TreeMap<>(Map.of(40.0, 100.0, 41.0, ZERO, 42.0, 0.6704,
                43.0, 0.1444,44.0, 2.1516, 45.0, ZERO, 46.0, 0.0041, 47.0, ZERO,
                48.0, 0.196)));
        ISOTOPE_TABLE.put("Cl", new TreeMap<>(Map.of(35.0, 75.8, 36.0, ZERO, 37.0, 24.2)));
        ISOTOPE_TABLE.put("Cu", new TreeMap<>(Map.of(63.0, 100.0, 64.0, ZERO, 65.0, 44.5713)));
        ISOTOPE_TABLE.put("F", new TreeMap<>(Map.of(19.0, 100.0)));
        ISOTOPE_TABLE.put("Fe", new TreeMap<>(Map.of(54.0, 6.3236, 55.0, ZERO, 56.0, 100.0,
                57.0, 2.3986, 58.0, 0.3053)));
        ISOTOPE_TABLE.put("H", new TreeMap<>(Map.of(1.0, 99.9855, 2.0, 0.0145)));
        ISOTOPE_TABLE.put("Hg", new TreeMap<>(Map.of(196.0, 0.5059, 197.0, ZERO, 198.0, 34.0641,
                199.0, 57.3356, 200.0, 77.9089, 201.0, 44.5194, 202.0, 100.0,
                203.0, ZERO, 204.0, 22.9342)));
        ISOTOPE_TABLE.put("I", new TreeMap<>(Map.of(127.0, 100.0)));
        ISOTOPE_TABLE.put("K", new TreeMap<>(Map.of(39.0, 100.0, 40.0, 0.0129, 41.0, 7.221)));
        ISOTOPE_TABLE.put("Mg", new TreeMap<>(Map.of(24.0, 100.0, 25.0, 12.6743, 26.0, 14.0684)));
        ISOTOPE_TABLE.put("Mn", new TreeMap<>(Map.of(55.0, 100.0)));
        ISOTOPE_TABLE.put("N", new TreeMap<>(Map.of(14.0, 99.6205, 15.0, 0.3795)));
        ISOTOPE_TABLE.put("Na", new TreeMap<>(Map.of(23.0, 100.0)));
        ISOTOPE_TABLE.put("O", new TreeMap<>(Map.of(16.0, 99.757, 17.0, 0.03835,18.0, 0.2045)));
        ISOTOPE_TABLE.put("P", new TreeMap<>(Map.of(31.0, 100.0)));
        ISOTOPE_TABLE.put("Pb", new TreeMap<>(Map.of(204.0, 2.6718, 205.0, ZERO, 206.0, 45.9924,
                207.0, 42.1756, 208.0, 100.0)));
        ISOTOPE_TABLE.put("Pt", new TreeMap<>(Map.of(190.0, 0.0296, 191.0, ZERO, 192.0, 2.3373,
                193.0, ZERO, 194.0, 97.3373, 195.0, 100.0, 196.0, 74.8521, 197.0, ZERO,
                198.0, 21.3018)));
        ISOTOPE_TABLE.put("S", new TreeMap<>(Map.of(32.0, 94.85, 33.0, 0.763,
                34.0, 4.365, 35.0, ZERO, 36.0, 0.0158)));
        ISOTOPE_TABLE.put("Sb", new TreeMap<>(Map.of(121.0, 100.0, 122.0, ZERO, 123.0, 74.5201)));
        ISOTOPE_TABLE.put("Se", new TreeMap<>(Map.of(74.0, 1.8145, 75.0, ZERO, 76.0, 18.1452,
                77.0, 15.3226, 78.0, 47.379, 79.0, ZERO, 80.0, 100.0, 81.0, ZERO,
                82.0, 18.9516)));
        ISOTOPE_TABLE.put("Si", new TreeMap<>(Map.of(28.0, 92.2545, 29.0, 4.672,30.0, 3.0735)));
        ISOTOPE_TABLE.put("Sn", new TreeMap<>(Map.of(116.0, 45.3704, 117.0, 23.7654, 118.0, 75.0,
                119.0, 26.5432, 120.0, 100.0, 121.0, ZERO, 122.0, 14.1975, 123.0, ZERO,
                124.0, 17.284)));
        ISOTOPE_TABLE.put("Th", new TreeMap<>(Map.of(232.0, 100.0)));
        ISOTOPE_TABLE.put("Ti", new TreeMap<>(Map.of(46.0, 10.8401, 47.0, 9.8916, 48.0, 100.0,
                49.0, 7.4526, 50.0, 7.3171)));
        ISOTOPE_TABLE.put("V", new TreeMap<>(Map.of(50.0, 0.2506, 51.0, 100.0)));
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
        return trim(distribution);
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

    private static SortedMap<Double, Double> trim(SortedMap<Double, Double> distribution) {
        SortedMap<Double, Double> trimmedDistribution = new TreeMap<>();
        for (Map.Entry<Double, Double> entry : distribution.entrySet())
            if (entry.getValue() >= MIN_INTENSITY)
                trimmedDistribution.put(entry.getKey(), entry.getValue());
        return trimmedDistribution;
    }
}
