package org.dulab.adapcompounddb.site.services.utils;

import junit.framework.TestCase;
import org.dulab.adapcompounddb.models.DbAndClusterValuePair;
import org.junit.Test;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSourceType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StatisticsUtilsTest extends TestCase
{

    private static final double EPS = 0.01;
    @Test
    public void testCalculateChiSquaredPermutationStatistics()
    {
        List<DbAndClusterValuePair> pairsList1 = new ArrayList<>();
        List<DbAndClusterValuePair> pairsList2 = new ArrayList<>();
        List<DbAndClusterValuePair> pairsList3 = new ArrayList<>();
        List<DbAndClusterValuePair> pairsList4 = new ArrayList<>();

//         // Test input 1
//        DbAndClusterValuePair single_quadrupole = new DbAndClusterValuePair(13, 10);
//        DbAndClusterValuePair gc_tof = new DbAndClusterValuePair(29, 0);
//        DbAndClusterValuePair single_quadruple = new DbAndClusterValuePair(13, 1);
//        DbAndClusterValuePair gc_ion_trap = new DbAndClusterValuePair(6, 0);
//        DbAndClusterValuePair gc_x_gc_tof = new DbAndClusterValuePair(7, 1);
//        pairsList.add(single_quadrupole);
//        pairsList.add(gc_tof);
//        pairsList.add(single_quadruple);
//        pairsList.add(gc_ion_trap);
//        pairsList.add(gc_x_gc_tof);

        // Test input 2
        DbAndClusterValuePair single_quadrupole = new DbAndClusterValuePair(13, 12);
        DbAndClusterValuePair gc_tof = new DbAndClusterValuePair(29, 0);
        DbAndClusterValuePair single_quadruple = new DbAndClusterValuePair(13, 2);
        DbAndClusterValuePair gc_ion_trap = new DbAndClusterValuePair(6, 0);
        DbAndClusterValuePair gc_x_gc_tof = new DbAndClusterValuePair(7, 1);
        pairsList2.add(single_quadrupole);
        pairsList2.add(gc_tof);
        pairsList2.add(single_quadruple);
        pairsList2.add(gc_ion_trap);
        pairsList2.add(gc_x_gc_tof);
        double actualPvalue2 = 0.0002;

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
        pairsList3.add(a3);
        pairsList3.add(b3);
        pairsList3.add(c3);
        pairsList3.add(d3);
        pairsList3.add(e3);
        pairsList3.add(f3);
        pairsList3.add(g3);
        pairsList3.add(h3);
        pairsList3.add(i3);
        pairsList3.add(j3);
        pairsList3.add(k3);
        double actualPvalue3 = 0.3086;

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
        pairsList4.add(a4);
        pairsList4.add(b4);
        pairsList4.add(c4);
        pairsList4.add(d4);
        pairsList4.add(e4);
        pairsList4.add(f4);
        pairsList4.add(g4);
        pairsList4.add(h4);
        pairsList4.add(i4);
        pairsList4.add(j4);
        pairsList4.add(k4);
        double actualPvalue4 = 0.905;


        int n =0;
        double sum = 0.0;
        int testTime = 1;
        List<Double> pvalueList = new ArrayList<>();
        for (int i=0; i<testTime; i++){
            double pValue = StatisticsUtils.calculateChiSquaredPermutationStatistics(pairsList3);
            sum += pValue;
            pvalueList.add(pValue);
//            System.out.println((pValue - 0.00000129) < EPS);
//            System.out.println(Math.abs(pValue - actualPvalue3) < EPS);
//            if (!(Math.abs(pValue - actualPvalue3) < EPS)){
//                n++;
//            }
            assertEquals(0.30276, pValue, EPS);
        }
        double mean = sum/testTime;



        double sd =0.0;

        for (double i: pvalueList){
            sd += Math.pow(i-mean, 2);
        }

        sd = Math.sqrt(sd/testTime);


//        System.out.println("total fail times are " + n);
        System.out.println("The average p-Value is " + mean);
        System.out.println("Actually p-value is " + actualPvalue3);
//        System.out.println("Standard deviatoin is " + sd);

    }
}