package org.dulab.adapcompounddb.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class MultinomialDistributionTest {

    private static final double EPS = 1e-3;

    @Test
    public void getPValue() {

        // This example is located at http://www.biostathandbook.com/exactgof.html
        double[] probabilities = {0.75, 0.25};
        int[] counts = {7, 5};

        MultinomialDistribution distribution = new MultinomialDistribution(probabilities, 12);

        assertEquals(0.103, distribution.getPMF(counts), EPS);
        assertEquals(0.189, distribution.getPValue(counts), EPS);
    }
}