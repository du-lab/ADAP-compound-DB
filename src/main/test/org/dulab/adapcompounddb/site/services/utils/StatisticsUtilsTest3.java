package org.dulab.adapcompounddb.site.services.utils;

import junit.framework.TestCase;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StatisticsUtilsTest3 extends TestCase
{

    private static final double EPS = 0.01;
    @Test
    public void testCalculateChiSquaredPermutationStatistics()
    {
        List<DbAndClusterValuePair> pairsList = new ArrayList<>();

        // Test input 529
        DbAndClusterValuePair a4 = new DbAndClusterValuePair(3, 1);
        DbAndClusterValuePair b4 = new DbAndClusterValuePair(1, 1);
        DbAndClusterValuePair c4 = new DbAndClusterValuePair(1, 0);
        DbAndClusterValuePair d4 = new DbAndClusterValuePair(1, 0);
        DbAndClusterValuePair e4 = new DbAndClusterValuePair(21, 9);
        DbAndClusterValuePair f4 = new DbAndClusterValuePair(2, 0);
        DbAndClusterValuePair g4 = new DbAndClusterValuePair(2, 0);
        DbAndClusterValuePair h4 = new DbAndClusterValuePair(1, 0);
        DbAndClusterValuePair i4 = new DbAndClusterValuePair(2, 0);
        DbAndClusterValuePair j4 = new DbAndClusterValuePair(1, 0);
        DbAndClusterValuePair k4 = new DbAndClusterValuePair(2, 0);
        pairsList.add(a4);
        pairsList.add(b4);
        pairsList.add(c4);
        pairsList.add(d4);
        pairsList.add(e4);
        pairsList.add(f4);
        pairsList.add(g4);
        pairsList.add(h4);
        pairsList.add(i4);
        pairsList.add(j4);
        pairsList.add(k4);
        double averagePvalue = 0.9509972;


//        int n =0;
//        double sum = 0.0;
//        int testTime = 1
//                ;
////        List<Double> pvalueList = new ArrayList<>();
//        for (int i=0; i<testTime; i++){
//            double pValue = StatisticsUtils.calculateChiSquaredPermutationStatistics(pairsList);
////            sum += pValue;
////            pvalueList.add(pValue);
////            System.out.println((pValue - 0.905) < EPS);
////            System.out.println(Math.abs(pValue - averagePvalue) < EPS);
////            if (!(Math.abs(pValue - averagePvalue) < EPS)){
////                n++;
////            }
//            assertEquals(0.9509972, pValue, EPS);
//        }

//        double mean = sum/testTime;

//        double sd =0.0;
//
//        for (double i: pvalueList){
//            sd += Math.pow(i-mean, 2);
//        }
//
//        sd = Math.sqrt(sd/testTime);


//        System.out.println("total fail times are " + n);
//        System.out.println("The average p-Value is " + mean);
//        System.out.println("Actually p-value is " + averagePvalue);
//        System.out.println("Standard deviatoin is " + sd);

        double pValue = StatisticsUtils.calculateChiSquaredPermutationStatistics(pairsList);
        assertEquals(averagePvalue, pValue, EPS);

    }
}