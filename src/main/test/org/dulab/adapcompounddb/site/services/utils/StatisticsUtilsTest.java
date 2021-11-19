package org.dulab.adapcompounddb.site.services.utils;

import junit.framework.TestCase;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.junit.Test;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSourceType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

//TODO Could you create methods `testCalculateChiSquaredPermutationStatistics1()`, `testCalculateChiSquaredPermutationStatistics2()`
// and `testCalculateChiSquaredPermutationStatistics3()` and completely remove classes `StatisticsUtilsTest2` and `StatisticsUtilsTest3`?
//TODO You don't need to extend TestCase. It'll work without it.
public class StatisticsUtilsTest extends TestCase
{

    private static final double EPS = 0.01;
    @Test
    public void testCalculateChiSquaredPermutationStatistics()
    {
        List<DbAndClusterValuePair> pairsList = new ArrayList<>();

        // Test input
        DbAndClusterValuePair single_quadrupole = new DbAndClusterValuePair(13, 12);
        DbAndClusterValuePair gc_tof = new DbAndClusterValuePair(29, 0);
        DbAndClusterValuePair single_quadruple = new DbAndClusterValuePair(13, 2);
        DbAndClusterValuePair gc_ion_trap = new DbAndClusterValuePair(6, 0);
        DbAndClusterValuePair gc_x_gc_tof = new DbAndClusterValuePair(7, 1);
        pairsList.add(single_quadrupole);
        pairsList.add(gc_tof);
        pairsList.add(single_quadruple);
        pairsList.add(gc_ion_trap);
        pairsList.add(gc_x_gc_tof);
        double averagePvalue = 0.0002;

//        int n =0;
//        double sum = 0.0;
//        int testTime = 50
//                ;
//        List<Double> pvalueList = new ArrayList<>();
//        for (int i=0; i<testTime; i++){
//            double pValue = StatisticsUtils.calculateChiSquaredPermutationStatistics(pairsList);
//            sum += pValue;
//            pvalueList.add(pValue);
//            System.out.println((pValue - 0.0002) < EPS);
//            System.out.println(Math.abs(pValue - averagePvalue) < EPS);
//            if (!(Math.abs(pValue - averagePvalue) < EPS)){
//                n++;
//            }
////
//        }
//        double mean = sum/testTime;
//
//        double sd =0.0;
//
//        for (double i: pvalueList){
//            sd += Math.pow(i-mean, 2);
//        }
//
//        sd = Math.sqrt(sd/testTime);
//
//
//        System.out.println("total fail times are " + n);
//        System.out.println("The average p-Value is " + mean);
//        System.out.println("Actually p-value is " + averagePvalue);
//        System.out.println("Standard deviatoin is " + sd);

        double pValue = StatisticsUtils.calculateChiSquaredPermutationStatistics(pairsList);
        assertEquals(averagePvalue, pValue, EPS);
    }
}