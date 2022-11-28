package org.dulab.adapcompounddb.site.services.utils;

import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.map.hash.TDoubleDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class IsotopicDistributionUtilsTest {

    @Test
    public void testCombinations52() {
        List<int[]> combinations = IsotopicDistributionUtils.getCombinations(5, 2);
        assertEquals(6, combinations.size());
        assertArrayEquals(combinations.get(0), new int[]{5, 0});
        assertArrayEquals(combinations.get(1), new int[]{4, 1});
        assertArrayEquals(combinations.get(2), new int[]{3, 2});
        assertArrayEquals(combinations.get(3), new int[]{2, 3});
        assertArrayEquals(combinations.get(4), new int[]{1, 4});
        assertArrayEquals(combinations.get(5), new int[]{0, 5});
    }

    @Test
    public void testCombinations33() {
        List<int[]> combinations = IsotopicDistributionUtils.getCombinations(3, 3);
        assertEquals(10, combinations.size());
        assertArrayEquals(combinations.get(0), new int[]{3, 0, 0});
        assertArrayEquals(combinations.get(1), new int[]{2, 1, 0});
        assertArrayEquals(combinations.get(2), new int[]{2, 0, 1});
        assertArrayEquals(combinations.get(3), new int[]{1, 2, 0});
        assertArrayEquals(combinations.get(4), new int[]{1, 1, 1});
        assertArrayEquals(combinations.get(5), new int[]{1, 0, 2});
        assertArrayEquals(combinations.get(6), new int[]{0, 3, 0});
        assertArrayEquals(combinations.get(7), new int[]{0, 2, 1});
        assertArrayEquals(combinations.get(8), new int[]{0, 1, 2});
        assertArrayEquals(combinations.get(9), new int[]{0, 0, 3});
    }

    @Test
    public void testDistributionOfAtoms() {
        SortedMap<Double, Double> atomDistribution = IsotopicDistributionUtils.ISOTOPE_TABLE.get("C");
        TDoubleDoubleHashMap distributionOfAtoms =
                IsotopicDistributionUtils.calculateDistributionOfAtoms(atomDistribution, 10);

        assertEquals(11, distributionOfAtoms.size());

        double[] keys = distributionOfAtoms.keys();
        double[] values = distributionOfAtoms.values();
        double[] sortedValues = IntStream.range(0, keys.length).boxed()
                .sorted(Comparator.comparingDouble(i -> keys[i]))
                .mapToDouble(i -> values[i])
                .toArray();

        assertEquals(100.0, sortedValues[0], 0.01);
        assertEquals(10.82, sortedValues[1], 0.01);
        assertEquals(0.53, sortedValues[2], 0.01);
        assertEquals(0.02, sortedValues[3], 0.01);
    }

    @Test
    public void testReadFormula() {
        TObjectIntHashMap<String> atoms = IsotopicDistributionUtils.readFormula("CH2O");
        assertEquals(3, atoms.size());
        assertEquals(1, atoms.get("C"));
        assertEquals(2, atoms.get("H"));
        assertEquals(1, atoms.get("O"));
    }

    @Test
    public void testCalculateDistribution() {
        TDoubleDoubleHashMap distribution = IsotopicDistributionUtils.calculateDistribution("CH2O");

        double[] keys = distribution.keys();
        double[] values = distribution.values();
        double[] sortedValues = IntStream.range(0, keys.length).boxed()
                .sorted(Comparator.comparingDouble(i -> keys[i]))
                .mapToDouble(i -> values[i])
                .toArray();

        assertEquals(100.0, sortedValues[0], 0.01);
        assertEquals(1.1537, sortedValues[1], 0.01);
        assertEquals(0.2013, sortedValues[2], 0.01);
//        assertEquals(0.0022, iterator.next().getValue(), 0.01);
    }

    @Test
    public void testCalculateDistributionAsArray() {
        double[] distribution = IsotopicDistributionUtils.calculateDistributionAsArray("CH2O");
        distribution = Arrays.copyOf(distribution, 4);
        assertArrayEquals(new double[]{100.0, 1.1537, 0.2013, 0.0022}, distribution, 0.01);
    }
}