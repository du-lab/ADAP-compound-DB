package org.dulab.adapcompounddb.site.services.utils;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.Combinatorics;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.dulab.adapcompounddb.models.MultinomialDistribution;

import java.util.*;

public class StatisticsUtils {

    private static Combinatorics combinatorics = new Combinatorics();

    /**
     * Calculates chi-squared statistics
     */
    public static double calculateChiSquaredStatistics(Collection<DbAndClusterValuePair> dbAndClusterValuePairs) {

        int freedomDegrees = dbAndClusterValuePairs.size() - 1;
        if (freedomDegrees == 0)
            return 1.0;

        int allDbSum = 0;
        int clusterSum = 0;
        for (DbAndClusterValuePair dbAndClusterValuePair : dbAndClusterValuePairs) {
            allDbSum += dbAndClusterValuePair.getDbValue();
            clusterSum += dbAndClusterValuePair.getClusterValue();
        }

        if (allDbSum == 0 || clusterSum == 0)
            throw new IllegalStateException("Sum of distribution values cannot be zero");

        double chiSquared = 0.0;
        for (DbAndClusterValuePair dbAndClusterValuePair : dbAndClusterValuePairs) {
            double p = (double) dbAndClusterValuePair.getDbValue() / allDbSum;
            double d = dbAndClusterValuePair.getClusterValue() - p * clusterSum;
            chiSquared = chiSquared + (d * d / (p * clusterSum));
        }

        return 1.0 - new ChiSquaredDistribution(freedomDegrees).cumulativeProbability(chiSquared);
    }

    /**
     * small number in chi-squared statistics with William's corrections
     */
    public static double calculateChiSquaredCorrection(Collection<DbAndClusterValuePair> dbAndClusterValuePairs) {

        int freedomDegrees = dbAndClusterValuePairs.size() - 1;
        if (freedomDegrees == 0)
            return 1.0;

        int allDbSum = 0;
        int clusterSum = 0;
        int categoryNums = 0;
        for (DbAndClusterValuePair dbAndClusterValuePair : dbAndClusterValuePairs) {
            allDbSum += dbAndClusterValuePair.getDbValue();
            clusterSum += dbAndClusterValuePair.getClusterValue();
            categoryNums++;
        }

        if (allDbSum == 0 || clusterSum == 0)
            throw new IllegalStateException("Sum of distribution values cannot be zero");

        double chiSquared = 0.0;
        for (DbAndClusterValuePair dbAndClusterValuePair : dbAndClusterValuePairs) {
            double p = (double) dbAndClusterValuePair.getDbValue() / allDbSum;
            double d = dbAndClusterValuePair.getClusterValue() - p * clusterSum;
            chiSquared = chiSquared + (d * d / (p * clusterSum));

        }

        // william's correction coefficient
        double q = 1.0 + (double) (categoryNums * categoryNums - 1) / (6 * clusterSum * freedomDegrees);

        return 1.0 - new ChiSquaredDistribution(freedomDegrees).cumulativeProbability(chiSquared / q);
    }

    /**
     * Calculates p-value of the Exact Goodness-of-fit test
     */
    public static double calculateExactTestStatistics(Collection<DbAndClusterValuePair> dbAndClusterValuePairs) {

        int allDbSum = 0;
        int clusterSum = 0;
        for (DbAndClusterValuePair dbAndClusterValuePair : dbAndClusterValuePairs) {
            allDbSum += dbAndClusterValuePair.getDbValue();
            clusterSum += dbAndClusterValuePair.getClusterValue();
        }

        double[] probabilities = new double[dbAndClusterValuePairs.size()];
        int[] counts = new int[dbAndClusterValuePairs.size()];
        Iterator<DbAndClusterValuePair> iterator = dbAndClusterValuePairs.iterator();
        for (int i = 0; iterator.hasNext(); ++i) {
            DbAndClusterValuePair dbAndClusterValuePair = iterator.next();
            probabilities[i] = (double) dbAndClusterValuePair.getDbValue() / allDbSum;
            counts[i] = dbAndClusterValuePair.getClusterValue();
        }

        MultinomialDistribution distribution = new MultinomialDistribution(probabilities, clusterSum, combinatorics);
        return distribution.getPValue(counts);
    }

    public static Map<String, DbAndClusterValuePair> calculateDbAndClusterDistribution(
            Map<String, Integer> dbCountMap, Map<String, Integer> clusterCountMap) {

        Map<String, DbAndClusterValuePair> countPairMap = new HashMap<>();

        for (Map.Entry<String, Integer> d : dbCountMap.entrySet()) {
            int dbValue = d.getValue();
            String key = d.getKey();
            Integer clusterValue = clusterCountMap.getOrDefault(key, 0);
            countPairMap.put(d.getKey(), new DbAndClusterValuePair(dbValue, clusterValue));
        }
        return countPairMap;
    }
}
