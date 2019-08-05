package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ServiceUtilsTest {

    private static final double EPS = 1e-5;

    @Test
    public void calculateChiSquaredStatisticsEmpty() {

        List<DbAndClusterValuePair> emptyList = new ArrayList<>(0);

        double pValue = ServiceUtils.calculateChiSquaredStatistics(emptyList);

        assertEquals(1.0, pValue, EPS);
    }

    @Test
    public void calculateChiSquaredStatisticsSingleton() {

        List<DbAndClusterValuePair> singleton = new ArrayList<>(1);
        singleton.add(new DbAndClusterValuePair(10, 2));

        double pValue = ServiceUtils.calculateChiSquaredStatistics(singleton);

        assertEquals(1.0, pValue, EPS);
    }

    @Test
    public void calculateChiSqauredStatistics() {

        List<DbAndClusterValuePair> pairs = new ArrayList<>(5);
        pairs.add(new DbAndClusterValuePair(1, 12));
        pairs.add(new DbAndClusterValuePair(2, 19));
        pairs.add(new DbAndClusterValuePair(5, 57));
        pairs.add(new DbAndClusterValuePair(3, 30));
        pairs.add(new DbAndClusterValuePair(1, 5));

        double pValue = ServiceUtils.calculateChiSquaredStatistics(pairs);

        // The expected value is calculated by calculating Chi-squared Goodness-of-fit test described at
        // https://www.stat.berkeley.edu/~stark/SticiGui/Text/chiSquare.htm
        assertEquals(	0.43932, pValue, EPS);
    }

    @Test
    public void calculateExactTestStatistics() {

        List<DbAndClusterValuePair> pairs = new ArrayList<>(5);
        pairs.add(new DbAndClusterValuePair(12, 1));
        pairs.add(new DbAndClusterValuePair(19, 2));
        pairs.add(new DbAndClusterValuePair(57, 5));
        pairs.add(new DbAndClusterValuePair(30, 3));
        pairs.add(new DbAndClusterValuePair(5, 1));

        double pValue = ServiceUtils.calculateExactTestStatistics(pairs);

        assertEquals(	0.80727, pValue, EPS);
    }

    @Test
    public void calculateDbAndClusterDistribution() {

        Map<String, Integer> dbCountMap = new HashMap<>();
        dbCountMap.put("value1", 1);
        dbCountMap.put("value2", 2);
        dbCountMap.put("value3", 3);
        dbCountMap.put("value4", 4);

        Map<String, Integer> clusterCountMap = new HashMap<>();
        clusterCountMap.put("value1", 10);
        clusterCountMap.put("value3", 30);

        Map<String, DbAndClusterValuePair> distribution =
                ServiceUtils.calculateDbAndClusterDistribution(dbCountMap, clusterCountMap);

        DbAndClusterValuePair pair1 = distribution.get("value1");
        assertEquals(1, pair1.getDbValue());
        assertEquals(10, pair1.getClusterValue());

        DbAndClusterValuePair pair2 = distribution.get("value2");
        assertEquals(2, pair2.getDbValue());
        assertEquals(0, pair2.getClusterValue());

        DbAndClusterValuePair pair3 = distribution.get("value3");
        assertEquals(3, pair3.getDbValue());
        assertEquals(30, pair3.getClusterValue());

        DbAndClusterValuePair pair4 = distribution.get("value4");
        assertEquals(4, pair4.getDbValue());
        assertEquals(0, pair4.getClusterValue());
    }
}