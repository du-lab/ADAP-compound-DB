package org.dulab.adapcompounddb.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Combinatorics {

    /**
     * Finds all combinations of k non-negative integers whose sum equals n
     * @param k number of elements in a combination
     * @param n sum of all integers in a combination
     * @return list of combinations
     */
    public static List<int[]> findCombinations(int k, int n) {
        List<int[]> combinations = new ArrayList<>();
        findCombinations(combinations, new int[k], 0, n);
        return combinations;
    }

    private static void findCombinations(List<int[]> allCombinations, int[] currentCombination,
                                        int index, int num) {

        if (Arrays.stream(currentCombination).sum() == num) {
            allCombinations.add(currentCombination.clone());
            return;
        }

        if (index >= currentCombination.length) return;

        for (int i = 0; i <= num; ++i) {
            currentCombination[index] = i;
            findCombinations(allCombinations, currentCombination, index + 1, num);
        }
    }
}
