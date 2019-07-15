package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

        assertEquals(0.999885, pValue, EPS);
    }
}