package org.dulab.adapcompounddb.site.services;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;

import java.util.*;

class ServiceUtils {

    static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    static double calculateChiSquaredStatistics(Collection<DbAndClusterValuePair> dbAndClusterValuePairs) {

        double clusterSum = 0.0;
        double alldbSum = 0.0;
        for (DbAndClusterValuePair pair : dbAndClusterValuePairs) {
            clusterSum += pair.getClusterValue();
            alldbSum += pair.getDbValue();
        }

        double k1 = Math.sqrt(alldbSum / clusterSum);
        double k2 = Math.sqrt(clusterSum / alldbSum);

        int freedomDegrees = 0;
        double chiSquareStatistics = 0.0;
        for (DbAndClusterValuePair pair : dbAndClusterValuePairs) {

            double d = k1 * pair.getClusterValue() - k2 * pair.getDbValue();

            chiSquareStatistics += d * d / (pair.getClusterValue() + pair.getDbValue());
            freedomDegrees++;
        }

        double pValue;
        if (freedomDegrees > 0) {
            pValue = 1 - new ChiSquaredDistribution(freedomDegrees)
                    .cumulativeProbability(chiSquareStatistics);
        } else {
            pValue = 1.0;
        }

        return pValue;
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
