package org.dulab.adapcompounddb.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CombinationOfIntegersTest {

    @Test
    public void next() {

        List<int[]> expected = new ArrayList<>(10);
        expected.add(new int[] {0, 0, 3});
        expected.add(new int[] {0, 1, 2});
        expected.add(new int[] {0, 2, 1});
        expected.add(new int[] {0, 3, 0});
        expected.add(new int[] {1, 0, 2});
        expected.add(new int[] {1, 1, 1});
        expected.add(new int[] {1, 2, 0});
        expected.add(new int[] {2, 0, 1});
        expected.add(new int[] {2, 1, 0});
        expected.add(new int[] {3, 0, 0});

        CombinationOfIntegers combinationOfIntegers = new CombinationOfIntegers(3, 3);

        for (int[] expectedCombination : expected) {
            assertArrayEquals(expectedCombination, combinationOfIntegers.next());
        }

        assertFalse(combinationOfIntegers.hasNext());
    }
}