package org.dulab.adapcompounddb.models;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MultinomialDistribution {

    private final Combinatorics combinatorics;

    private final double[] probabilities;
    private final int numTrials;

    public MultinomialDistribution(double[] probabilities, int numTrials, Combinatorics combinatorics) {
        this.combinatorics = combinatorics;
        this.probabilities = probabilities;
        this.numTrials = numTrials;
    }

    /**
     * Calculates the probability mass function of given distribution
     *
     * @param counts array of non-negative integers whose sum is numTrials
     * @return probability
     */
    public double getPMF(int[] counts) {

        if (counts.length != probabilities.length)
            throw new IllegalArgumentException("The number of counts doesn't match the number of probabilities");

        if (Arrays.stream(counts).sum() != numTrials)
            throw new IllegalArgumentException("The sum of counts doesn't match numTrials");

        double logP = CombinatoricsUtils.factorialLog(numTrials);
        for (int i = 0; i < counts.length; ++i) {
            logP -= CombinatoricsUtils.factorialLog(counts[i]);
            logP += counts[i] * Math.log(probabilities[i]);
        }

        return Math.exp(logP);
    }

    /**
     * Calculates p-value, using the method of small P values.
     *
     * @param counts array of non-negative integers whose sum is numTrials
     * @return p-value
     */
    public double getPValue(int[] counts) {

        double thresholdP = getPMF(counts);

        Iterator<int[]> iterator = new CombinationOfIntegers(probabilities.length, numTrials);

        double sum = 0.0;
        while (iterator.hasNext()) {
            int[] combination = iterator.next();
            double p = getPMF(combination);
            if (p <= thresholdP)
                sum += p;
        }

        return sum;
    }


}
