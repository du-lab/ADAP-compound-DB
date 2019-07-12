package org.dulab.adapcompounddb.site.services;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        for (DbAndClusterValuePair pair : dbAndClusterValuePairs) {

            double c = (double) pair.getClusterValue();
            double a = (double) pair.getDbValue();

            // calculate chi-squared statistics
            double sum = (c - a) * (c - a) / (a);  //TODO: we need to fix this formula
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
}
