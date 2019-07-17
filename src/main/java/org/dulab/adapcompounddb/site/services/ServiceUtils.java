package org.dulab.adapcompounddb.site.services;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class ServiceUtils {

    static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    static double calculateChiSquaredStatistics(Collection<DbAndClusterValuePair> dbAndClusterValuePairs) {

        int freedomDegrees = 0;
        double chiSquareStatistics = 0.0;
        double pValue;
        double clusterSum = 0.0;
        double alldbSum = 0.0;

        for (DbAndClusterValuePair pair : dbAndClusterValuePairs) {

            clusterSum = clusterSum + (double) pair.getClusterValue();
            alldbSum = alldbSum + (double) pair.getDbValue();
        }


        for (DbAndClusterValuePair pair : dbAndClusterValuePairs) {

            double c = (double) pair.getClusterValue();
            double d = (double) pair.getDbValue();

            double sum = (c/clusterSum - d/alldbSum) * (c/clusterSum - d/alldbSum) / (d/alldbSum);
            chiSquareStatistics = chiSquareStatistics + sum;
            freedomDegrees++;
        }
        if (freedomDegrees > 1) {
            pValue = 1 - new ChiSquaredDistribution(freedomDegrees - 1)
                    .cumulativeProbability(chiSquareStatistics);
        } else {
            pValue = 1.0;
        }

        return pValue;
    }


    static Map<String, DbAndClusterValuePair> calculateDbAndClusterDistribution(
            Map<String, Integer> dbCountMap, Map<String, Integer> clusterCountMap) {

        return null;
    }
}
