package org.dulab.adapcompounddb.site.services.utils;

import org.junit.Test;

import java.util.*;

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
        SortedMap<Double, Double> distributionOfAtoms =
                IsotopicDistributionUtils.calculateDistributionOfAtoms(atomDistribution, 10);

        assertEquals(11, distributionOfAtoms.size());

        Iterator<Map.Entry<Double, Double>> iterator = distributionOfAtoms.entrySet().iterator();
        assertEquals(100.0, iterator.next().getValue(), 0.01);
        assertEquals(10.82, iterator.next().getValue(), 0.01);
        assertEquals(0.53, iterator.next().getValue(), 0.01);
        assertEquals(0.02, iterator.next().getValue(), 0.01);
    }

    @Test
    public void testReadFormula() {
        Map<String, Integer> atoms = IsotopicDistributionUtils.readFormula("CH2O");
        assertEquals(3, atoms.size());
        assertEquals(Integer.valueOf(1), atoms.get("C"));
        assertEquals(Integer.valueOf(2), atoms.get("H"));
        assertEquals(Integer.valueOf(1), atoms.get("O"));
    }

    @Test
    public void testCalculateDistribution() {
        SortedMap<Double, Double> distribution = IsotopicDistributionUtils.calculateDistribution("CH2O");

        Iterator<Map.Entry<Double, Double>> iterator = distribution.entrySet().iterator();
        assertEquals(100.0, iterator.next().getValue(), 0.01);
        assertEquals(1.1537, iterator.next().getValue(), 0.01);
        assertEquals(0.2013, iterator.next().getValue(), 0.01);
        assertEquals(0.0022, iterator.next().getValue(), 0.01);
    }

    @Test
    public void testCalculateDistributionAsArray() {
        double[] distribution = IsotopicDistributionUtils.calculateDistributionAsArray("CH2O");
        distribution = Arrays.copyOf(distribution, 4);
        assertArrayEquals(new double[]{100.0, 1.1537, 0.2013, 0.0022}, distribution, 0.01);
    }
}