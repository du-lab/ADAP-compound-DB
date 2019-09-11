package org.dulab.adapcompounddb.site.services;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.Combinatorics;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.dulab.adapcompounddb.models.MultinomialDistribution;

import java.util.*;

class ServiceUtils {

    private static final Logger LOGGER = LogManager.getLogger(ServiceUtils.class);

    private static Combinatorics combinatorics = new Combinatorics();

    static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    static double calculateChiSquaredStatistics(Collection<DbAndClusterValuePair> dbAndClusterValuePairs) {

        int freedomDegrees = dbAndClusterValuePairs.size() - 1;
        if (freedomDegrees == 0)
            return 1.0;

        int allDbSum = dbAndClusterValuePairs.stream()
                .mapToInt(DbAndClusterValuePair::getDbValue)
                .sum();

        int clusterSum = dbAndClusterValuePairs.stream()
                .mapToInt(DbAndClusterValuePair::getClusterValue)
                .sum();

        if (allDbSum == 0 || clusterSum == 0)
            throw new IllegalStateException("Sum of distribution values cannot be zero");

        double chiSquared = dbAndClusterValuePairs.stream()
                .mapToDouble(pair -> {
                    double p = (double) pair.getDbValue() / allDbSum;
                    double d = pair.getClusterValue() - p * clusterSum;
                    return d * d / (p * clusterSum);
                })
                .sum();

        return 1.0 - new ChiSquaredDistribution(freedomDegrees).cumulativeProbability(chiSquared);
    }

    /**
     * Calculates p-value of the Exact Goodness-of-fit test
     */
    static double calculateExactTestStatistics(Collection<DbAndClusterValuePair> dbAndClusterValuePairs) {

        LOGGER.info("Calculating Exact Goodness-of-fit test...");

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

    static Map<String, DbAndClusterValuePair> calculateDbAndClusterDistribution(
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
