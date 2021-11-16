package org.dulab.adapcompounddb.site.services.utils;

import junit.framework.TestCase;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StatisticsUtilsTest2 extends TestCase
{

    private static final double EPS = 0.01;
    @Test
    public void testCalculateChiSquaredPermutationStatistics()
    {

        List<DbAndClusterValuePair> pairsList = new ArrayList<>();

        // Test input 277
        DbAndClusterValuePair a3 = new DbAndClusterValuePair(38, 12);
        DbAndClusterValuePair b3 = new DbAndClusterValuePair(2, 0);
        DbAndClusterValuePair c3 = new DbAndClusterValuePair(1, 1);
        DbAndClusterValuePair d3 = new DbAndClusterValuePair(1, 0);
        DbAndClusterValuePair e3 = new DbAndClusterValuePair(1, 1);
        DbAndClusterValuePair f3 = new DbAndClusterValuePair(4, 0);
        DbAndClusterValuePair g3 = new DbAndClusterValuePair(1, 0);
        DbAndClusterValuePair h3 = new DbAndClusterValuePair(1, 0);
        DbAndClusterValuePair i3 = new DbAndClusterValuePair(1, 0);
        DbAndClusterValuePair j3 = new DbAndClusterValuePair(1, 0);
        DbAndClusterValuePair k3 = new DbAndClusterValuePair(11, 0);
        pairsList.add(a3);
        pairsList.add(b3);
        pairsList.add(c3);
        pairsList.add(d3);
        pairsList.add(e3);
        pairsList.add(f3);
        pairsList.add(g3);
        pairsList.add(h3);
        pairsList.add(i3);
        pairsList.add(j3);
        pairsList.add(k3);
        double averagePvalue = 0.3034424;

//        int n =0;
//        double sum = 0.0;
//        int testTime = 50
                ;
//        List<Double> pvalueList = new ArrayList<>();
//        for (int i=0; i<testTime; i++){
//            double pValue = StatisticsUtils.calculateChiSquaredPermutationStatistics(pairsList);
//            sum += pValue;
//            pvalueList.add(pValue);
//            System.out.println((pValue - 0.3086) < EPS);
//            System.out.println(Math.abs(pValue - averagePvalue) < EPS);
//            if (!(Math.abs(pValue - averagePvalue) < EPS)){
//                n++;
//            }
////
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