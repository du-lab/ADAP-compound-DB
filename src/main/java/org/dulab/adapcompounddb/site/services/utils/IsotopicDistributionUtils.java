package org.dulab.adapcompounddb.site.services.utils;

import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TDoubleDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TDoubleProcedure;
import org.apache.commons.math3.special.Gamma;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.persistence.jpa.config.TenantTableDiscriminator;

import java.util.*;
import java.util.stream.IntStream;

public class IsotopicDistributionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsotopicDistributionUtils.class);

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
        ISOTOPE_TABLE.put("Ba", new TreeMap<>(Map.of(130.0, 0.1534, 131.0, ZERO, 132.0, 0.1395,
                133.0, ZERO, 134.0, 3.3752, 135.0, 9.1911, 136.0, 10.9484,
                137.0, 15.6625, 138.0, 100.0)));
        ISOTOPE_TABLE.put("Bi", new TreeMap<>(Map.of(209.0, 100.0)));
        ISOTOPE_TABLE.put("Br", new TreeMap<>(Map.of(79.0, 50.65, 80.0, ZERO, 81.0, 49.35)));
        ISOTOPE_TABLE.put("C", new TreeMap<>(Map.of(12.0, 100.0, 13.0, 1.0816)));
        ISOTOPE_TABLE.put("Ca", new TreeMap<>(Map.of(40.0, 100.0, 41.0, ZERO, 42.0, 0.6704,
                43.0, 0.1444, 44.0, 2.1516, 45.0, ZERO, 46.0, 0.0041, 47.0, ZERO,
                48.0, 0.196)));
        ISOTOPE_TABLE.put("Cl", new TreeMap<>(Map.of(35.0, 75.8, 36.0, ZERO, 37.0, 24.2)));
        ISOTOPE_TABLE.put("Cr", new TreeMap<>(Map.of(50.0, 5.1916, 51.0, ZERO, 52.0, 100.0,
                53.0, 11.3379, 54.0, 2.8166)));
        ISOTOPE_TABLE.put("Cu", new TreeMap<>(Map.of(63.0, 100.0, 64.0, ZERO, 65.0, 44.5713)));
        ISOTOPE_TABLE.put("D", new TreeMap<>(Map.of(2.0, 100.0)));
        ISOTOPE_TABLE.put("Er", new TreeMap<>(Map.of(162.0, 0.4167, 163.0, ZERO, 164.0, 4.7917,
                165.0, ZERO, 166.0, 100.0, 167.0, 68.3036, 168.0, 79.7619, 169.0, ZERO,
                170.0, 44.3452)));
        ISOTOPE_TABLE.put("F", new TreeMap<>(Map.of(19.0, 100.0)));
        ISOTOPE_TABLE.put("Fe", new TreeMap<>(Map.of(54.0, 6.3236, 55.0, ZERO, 56.0, 100.0,
                57.0, 2.3986, 58.0, 0.3053)));
        ISOTOPE_TABLE.put("Ga", new TreeMap<>(Map.of(69.0, 100.0, 70.0, ZERO, 71.0, 66.3894)));
        ISOTOPE_TABLE.put("Gd", new TreeMap<>(Map.of(152.0, 0.8052, 153.0, ZERO, 154.0, 8.7762,
                155.0, 59.5813, 156.0, 82.4074, 157.0, 63.0032, 158.0, 100.0,
                159.0, ZERO, 160.0, 88.0032)));
        ISOTOPE_TABLE.put("H", new TreeMap<>(Map.of(1.0, 99.9855, 2.0, 0.0145)));
        ISOTOPE_TABLE.put("Hg", new TreeMap<>(Map.of(196.0, 0.5059, 197.0, ZERO, 198.0, 34.0641,
                199.0, 57.3356, 200.0, 77.9089, 201.0, 44.5194, 202.0, 100.0,
                203.0, ZERO, 204.0, 22.9342)));
        ISOTOPE_TABLE.put("I", new TreeMap<>(Map.of(127.0, 100.0)));
        ISOTOPE_TABLE.put("K", new TreeMap<>(Map.of(39.0, 100.0, 40.0, 0.0129, 41.0, 7.221)));
        ISOTOPE_TABLE.put("Lu", new TreeMap<>(Map.of(175.0, 100.0, 176.0, 2.6694)));
        ISOTOPE_TABLE.put("Mg", new TreeMap<>(Map.of(24.0, 100.0, 25.0, 12.6743, 26.0, 14.0684)));
        ISOTOPE_TABLE.put("Mn", new TreeMap<>(Map.of(55.0, 100.0)));
        ISOTOPE_TABLE.put("N", new TreeMap<>(Map.of(14.0, 99.6205, 15.0, 0.3795)));
        ISOTOPE_TABLE.put("Na", new TreeMap<>(Map.of(23.0, 100.0)));
        ISOTOPE_TABLE.put("Ni", new TreeMap<>(Map.of(58.0, 100.0, 59.0, ZERO, 60.0, 38.2306,
                61.0, 1.6552, 62.0, 5.2585, 63.0, ZERO, 64.0, 1.3329)));
        ISOTOPE_TABLE.put("O", new TreeMap<>(Map.of(16.0, 99.757, 17.0, 0.03835, 18.0, 0.2045)));
        ISOTOPE_TABLE.put("Os", new TreeMap<>(Map.of(184.0, 0.0488, 185.0, ZERO, 186.0, 3.8537,
                187.0, 3.9024, 188.0, 32.439, 189.0, 39.2683, 190.0, 64.3902,
                191.0, ZERO, 192.0, 100.0)));
        ISOTOPE_TABLE.put("P", new TreeMap<>(Map.of(31.0, 100.0)));
        ISOTOPE_TABLE.put("Pb", new TreeMap<>(Map.of(204.0, 2.6718, 205.0, ZERO, 206.0, 45.9924,
                207.0, 42.1756, 208.0, 100.0)));
        ISOTOPE_TABLE.put("Pt", new TreeMap<>(Map.of(190.0, 0.0296, 191.0, ZERO, 192.0, 2.3373,
                193.0, ZERO, 194.0, 97.3373, 195.0, 100.0, 196.0, 74.8521, 197.0, ZERO,
                198.0, 21.3018)));
        ISOTOPE_TABLE.put("Re", new TreeMap<>(Map.of(185.0, 59.7444, 186.0, ZERO, 187.0, 100.0)));
        ISOTOPE_TABLE.put("S", new TreeMap<>(Map.of(32.0, 94.85, 33.0, 0.763,
                34.0, 4.365, 35.0, ZERO, 36.0, 0.0158)));
        ISOTOPE_TABLE.put("Sb", new TreeMap<>(Map.of(121.0, 100.0, 122.0, ZERO, 123.0, 74.5201)));
        ISOTOPE_TABLE.put("Se", new TreeMap<>(Map.of(74.0, 1.8145, 75.0, ZERO, 76.0, 18.1452,
                77.0, 15.3226, 78.0, 47.379, 79.0, ZERO, 80.0, 100.0, 81.0, ZERO,
                82.0, 18.9516)));
        ISOTOPE_TABLE.put("Si", new TreeMap<>(Map.of(28.0, 92.2545, 29.0, 4.672, 30.0, 3.0735)));
        ISOTOPE_TABLE.put("Sn", new TreeMap<>(Map.of(116.0, 45.3704, 117.0, 23.7654, 118.0, 75.0,
                119.0, 26.5432, 120.0, 100.0, 121.0, ZERO, 122.0, 14.1975, 123.0, ZERO,
                124.0, 17.284)));
        ISOTOPE_TABLE.put("Th", new TreeMap<>(Map.of(232.0, 100.0)));
        ISOTOPE_TABLE.put("Ti", new TreeMap<>(Map.of(46.0, 10.8401, 47.0, 9.8916, 48.0, 100.0,
                49.0, 7.4526, 50.0, 7.3171)));
        ISOTOPE_TABLE.put("Tl", new TreeMap<>(Map.of(203.0, 41.8842, 204.0, ZERO, 205.0, 100.0)));
        ISOTOPE_TABLE.put("V", new TreeMap<>(Map.of(50.0, 0.2506, 51.0, 100.0)));
        ISOTOPE_TABLE.put("W", new TreeMap<>(Map.of(180.0, 0.4239, 181.0, ZERO, 182.0, 85.7515,
                183.0, 46.6254, 184.0, 100.0, 185.0, ZERO, 186.0, 76.9482)));
        ISOTOPE_TABLE.put("Zn", new TreeMap<>(Map.of(64.0, 100.0, 65.0, ZERO, 66.0, 57.4074,
                67.0, 8.4362, 68.0, 38.6831, 69.0, ZERO, 70.0, 1.2346)));
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
    public static TDoubleDoubleHashMap calculateDistributionOfAtoms(
            SortedMap<Double, Double> distribution, int n) {

        int k = distribution.size();
        List<int[]> combinations = getCombinations(n, k);

        TDoubleDoubleHashMap outputDistribution = new TDoubleDoubleHashMap();
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
            double value = Math.exp(lnCoefficient);
            outputDistribution.adjustOrPutValue(mass, value, value);
//            outputDistribution.merge(mass, Math.exp(lnCoefficient), Double::sum);
        }

        scale(outputDistribution);

        return outputDistribution;
    }

    /**
     * Returns isotopic distribution of two different atoms
     *
     * @param distribution1 distribution of the first atom
     * @param distribution2 distribution of the second atom
     * @return isotopic distribution
     */
    public static TDoubleDoubleHashMap calculateDistributionOfTwoAtoms(
            TDoubleDoubleHashMap distribution1, TDoubleDoubleHashMap distribution2) {

        if (distribution1.isEmpty()) return distribution2;
        if (distribution2.isEmpty()) return distribution1;

        TDoubleDoubleHashMap outputDistribution = new TDoubleDoubleHashMap();
        for (TDoubleDoubleIterator iterator1 = distribution1.iterator(); iterator1.hasNext();) {
            iterator1.advance();
            for (TDoubleDoubleIterator iterator2 = distribution2.iterator(); iterator2.hasNext();) {
                iterator2.advance();
                double mass = iterator1.key() + iterator2.key();
                double value = iterator1.value() * iterator2.value();
                outputDistribution.adjustOrPutValue(mass, value, value);
            }
        }

        scale(outputDistribution);

        return outputDistribution;
    }

    /**
     * Returns the isotopic distribution for the given formula
     *
     * @param formula molecular formula
     * @return isotopic distribution
     */
    public static TDoubleDoubleHashMap calculateDistribution(String formula) {
        TObjectIntHashMap<String> atoms = readFormula(formula);
        TDoubleDoubleHashMap distribution = new TDoubleDoubleHashMap();
        for (TObjectIntIterator<String> atomIterator = atoms.iterator(); atomIterator.hasNext(); ) {
            atomIterator.advance();
            SortedMap<Double, Double> atomDistribution = ISOTOPE_TABLE.get(atomIterator.key());
            if (atomDistribution == null) {
                LOGGER.warn("Unknown distribution of atom " + atomIterator.key());
                return new TDoubleDoubleHashMap(0);
            }

            distribution = calculateDistributionOfTwoAtoms(
                    distribution, calculateDistributionOfAtoms(atomDistribution, atomIterator.value()));
        }
        return trim(distribution);
    }

    public static double[] calculateDistributionAsArray(String formula) {
        TDoubleDoubleHashMap distribution = calculateDistribution(formula);
        double[] keys = distribution.keys();
        double[] values = distribution.values();
        return IntStream.range(0, keys.length)
                .boxed()
                .sorted(Comparator.comparingDouble(i -> keys[i]))
                .mapToDouble(i -> values[i])
                .toArray();
//        return distribution.values().stream().mapToDouble(Double::doubleValue).toArray();
    }

    public static TObjectIntHashMap<String> readFormula(String formula) {
        TObjectIntHashMap<String> atoms = new TObjectIntHashMap<>();

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


    private static void scale(TDoubleDoubleHashMap distribution) {
        Arrays.stream(distribution.values())
                .max()
                .ifPresent(max -> distribution.transformValues(v -> v * 100 / max));
//        distribution.values()
//                .stream()
//                .mapToDouble(Double::doubleValue)
//                .max()
//                .ifPresent(max -> distribution.replaceAll((key, value) -> value * 100 / max));
    }

    private static TDoubleDoubleHashMap trim(TDoubleDoubleHashMap distribution) {
        TDoubleDoubleHashMap trimmedDistribution = new TDoubleDoubleHashMap();
        for (TDoubleDoubleIterator iterator = distribution.iterator(); iterator.hasNext();) {
            iterator.advance();
            if (iterator.value() >= MIN_INTENSITY)
                trimmedDistribution.put(iterator.key(), iterator.value());
        }
        return trimmedDistribution;
    }
}
