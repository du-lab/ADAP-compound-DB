package org.dulab.adapcompounddb.models;

import org.paukov.combinatorics3.Generator;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class generates all the ways to represent number N by a sum of K non-negative integers.
 * <p>
 * For instance, number 3 can be represented by a sum of 2 non-negative integers in the following ways:
 * (3, 0) (2, 1) (1, 2) (0, 3).
 * <p>
 * These representations can be written in the following form:
 * (1+1+1,) (1+1,1) (1,1+1) (,1+1+1)
 * that are completely defined by the positions of the comma. The comma positions are given by all K-1 combinations
 * of numbers [0, 1, ..., N] with repetition. In the example above, those positions are (0) (1) (2) (3).
 * <p>
 * For details, see https://www.geeksforgeeks.org/different-ways-to-represent-n-as-sum-of-k-non-zero-integers/
 */
public class CombinationOfIntegers implements Iterator<int[]> {

    private final int numIntegers;
    private final int sum;
    private final Iterator<List<Integer>> commaPositionsIterator;

    public CombinationOfIntegers(int numIntegers, int sum) {

        List<Integer> possibleCommaPositions = IntStream.range(0, sum + 1)
                .boxed()
                .collect(Collectors.toList());

        this.numIntegers = numIntegers;
        this.sum = sum;

        this.commaPositionsIterator = Generator.combination(possibleCommaPositions)
                .multi(numIntegers - 1)
                .iterator();
    }

    public boolean hasNext() {
        return commaPositionsIterator.hasNext();
    }

    public int[] next() {

        if (!commaPositionsIterator.hasNext())
            return null;

        List<Integer> commaPositions = commaPositionsIterator.next();
        if (commaPositions.isEmpty())
            return new int[]{sum};

        int[] partition = new int[numIntegers];
        partition[0] = commaPositions.get(0);
        for (int i = 1; i < commaPositions.size(); ++i)
            partition[i] = commaPositions.get(i) - commaPositions.get(i - 1);
        partition[numIntegers - 1] = sum - commaPositions.get(commaPositions.size() - 1);

        return partition;
    }
}
