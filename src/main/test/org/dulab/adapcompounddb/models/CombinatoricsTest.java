package org.dulab.adapcompounddb.models;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CombinatoricsTest {

    @Test
    public void findCombinations() {

        List<int[]> combinations = Combinatorics.findCombinations(3, 5);

        assertEquals(21, combinations.size());
        combinations.forEach(c -> assertEquals(5, Arrays.stream(c).sum()));
    }
}