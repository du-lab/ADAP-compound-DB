package org.dulab.adapcompounddb.models;

import java.util.*;

public class Combinatorics {

    private Map<Long, List<int[]>> savedCombinations = new HashMap<>();

    /**
     * Finds all combinations of k non-negative integers whose sum equals n
     *
     * @param k number of elements in a combination
     * @param n sum of all integers in a combination
     * @return list of combinations
     */
    public List<int[]> findCombinations(int k, int n) {

        long cantorNumber = getCantorPairing(k, n);
        List<int[]> combinations = savedCombinations.get(cantorNumber);
        if (combinations != null)
            return combinations;

        combinations = new ArrayList<>();
        findCombinations(combinations, new int[k], 0, 0, n);
        savedCombinations.put(cantorNumber, combinations);
        return combinations;
    }

    private void findCombinations(List<int[]> allCombinations, int[] currentCombination,
                                         int index, int sum, int num) {

        if (sum == num) {
            allCombinations.add(currentCombination.clone());
            return;
        }

        if (index >= currentCombination.length || sum > num) return;

        for (int i = 0; i <= num; ++i) {
            currentCombination[index] = i;
            findCombinations(allCombinations, currentCombination, index + 1, sum + i, num);
        }

        currentCombination[index] = 0;
    }

    /**
     * Mapping NxN -> N
     *
     * For two given integers, produces a new unique integer. See "Cantor pairing function"
     *
     * @param k1 integer
     * @param k2 integer
     * @return integer
     */
    private long getCantorPairing(long k1, long k2) {
        return (k1 + k2) * (k1 + k2 + 1) / 2 + k2;
    }
}
