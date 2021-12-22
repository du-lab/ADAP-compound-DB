package org.dulab.adapcompounddb.site.services.utils;

import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StatisticsUtilsTest
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
        double averagePvalue = 9.25999996252358E-4;

//        runMultipleTimeTest(pairsList,averagePvalue);

        long beiginTime1 = System.currentTimeMillis();
        double pValue1 = StatisticsUtils.calculateChiSquaredPermutationStatistics(pairsList);
        long endTime1 = System.currentTimeMillis();
        System.out.println("test 1 permutation cost " + (endTime1-beiginTime1) / 1000f);
        System.out.println("test 1 pvalue of permutation test is " + pValue1);

        long beginTime2 = System.currentTimeMillis();
        double pValue2 = StatisticsUtils.calculateExactTestStatistics(pairsList);
        long endTime2 = System.currentTimeMillis();
        System.out.println("test 1 exact test cost " + (endTime2-beginTime2) / 1000f);
        System.out.println("test 1 pvalue of exact test is " + pValue2);

        long beginTime3 = System.currentTimeMillis();
        double pValue3 = StatisticsUtils.calculateChiSquaredStatistics(pairsList);
        long endTime3 = System.currentTimeMillis();
        System.out.println("test 1 chisquare test cost " + (endTime3-beginTime3) / 1000f);
        System.out.println("test 1 pvalue of chisquare test is " + pValue3);

        assertEquals(averagePvalue, pValue1, EPS);
    }

    @Test
    public void testCalculateChiSquaredPermutationStatistics2(){
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
        double averagePvalue = 0.3804863995313644;

//        runMultipleTimeTest(pairsList,averagePvalue);

        long beginTime1 = System.currentTimeMillis();
        double pValue1 = StatisticsUtils.calculateChiSquaredPermutationStatistics(pairsList);
        long endTime1 = System.currentTimeMillis();
        System.out.println("test 2 permutation cost " + (endTime1-beginTime1) / 1000f);
        System.out.println("test 2 pvalue of permutation test is " + pValue1);

        long beginTime2 = System.currentTimeMillis();
        double pValue2 = StatisticsUtils.calculateExactTestStatistics(pairsList);
        long endTime2 = System.currentTimeMillis();
        System.out.println("test 2 exact test cost " + (endTime2-beginTime2) / 1000f);
        System.out.println("test 2 pvalue of exact test is " + pValue2);

        long beginTime3 = System.currentTimeMillis();
        double pValue3 = StatisticsUtils.calculateChiSquaredStatistics(pairsList);
        long endTime3 = System.currentTimeMillis();
        System.out.println("test 2 chisquare test cost " + (endTime3-beginTime3) / 1000f);
        System.out.println("test 2 pvalue of chisquare test is " + pValue3);

        assertEquals(averagePvalue, pValue1, EPS);
    }

    @Test
    public void testCalculateChiSquaredPermutationStatistics3(){
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
        double averagePvalue = 0.9213051998615265;

//        runMultipleTimeTest(pairsList,averagePvalue);

        long beginTime1 = System.currentTimeMillis();
        double pValue1 = StatisticsUtils.calculateChiSquaredPermutationStatistics(pairsList);
        long endTime1 = System.currentTimeMillis();
        System.out.println("test 3 permutation cost " + (endTime1-beginTime1) / 1000f);
        System.out.println("test 3 pvalue of permutation test is " + pValue1);

        long beginTime2 = System.currentTimeMillis();
        double pValue2 = StatisticsUtils.calculateExactTestStatistics(pairsList);
        long endTime2 = System.currentTimeMillis();
        System.out.println("test 3 exact test cost " + (endTime2-beginTime2) / 1000f);
        System.out.println("test 3 pvalue of exact test is " + pValue2);

        long beginTime3 = System.currentTimeMillis();
        double pValue3 = StatisticsUtils.calculateChiSquaredStatistics(pairsList);
        long endTime3 = System.currentTimeMillis();
        System.out.println("test 3 chisquare test cost " + (endTime3-beginTime3) / 1000f);
        System.out.println("test 3 pvalue of chisquare test is " + pValue3);

        assertEquals(averagePvalue, pValue1, EPS);

    }


    // function for running permutation test multiple times
    public void runMultipleTimeTest(List<DbAndClusterValuePair> pairsList, Double averagePvalue){
        int n =0;
        double sum = 0.0;
        int testTime = 50
                ;
        List<Double> pvalueList = new ArrayList<>();
        for (int i=0; i<testTime; i++){
            double pValue = StatisticsUtils.calculateChiSquaredPermutationStatistics(pairsList);
            sum += pValue;
            pvalueList.add(pValue);
            System.out.println((pValue - 0.0002) < EPS);
            System.out.println(Math.abs(pValue - averagePvalue) < EPS);
            if (!(Math.abs(pValue - averagePvalue) < EPS)){
                n++;
            }
//
        }
        double mean = sum/testTime;

        double sd =0.0;

        for (double i: pvalueList){
            sd += Math.pow(i-mean, 2);
        }

        sd = Math.sqrt(sd/testTime);

        System.out.println("total fail times are " + n);
        System.out.println("The average p-Value is " + mean);
        System.out.println("Actually p-value is " + averagePvalue);
        System.out.println("Standard deviatoin is " + sd);
    }
}