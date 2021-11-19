package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.Peak;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

//import static org.dulab.adapcompounddb.site.controllers.ControllerUtils.peaksToJson;
import static org.junit.Assert.*;

public class ControllerUtilsTest {

    @Test
    public void peaksToJsonTest() {

        //TODO Why have you commented this out?
        // Null value check
//        assertEquals("", peaksToJson(null));

        // Empty list check
//        assertEquals("", peaksToJson(new ArrayList<>(0)));

        // List of two peaks
        Peak peak1 = new Peak();
        peak1.setMz(40.0);
        peak1.setIntensity(100.0);

        Peak peak2 = new Peak();
        peak2.setMz(41.0);
        peak2.setIntensity(10.0);

//        assertEquals("[[40.0,100.0],[41.0,10.0]]", peaksToJson(Arrays.asList(peak1, peak2)));

    }
}