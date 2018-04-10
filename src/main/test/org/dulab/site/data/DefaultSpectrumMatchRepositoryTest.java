package org.dulab.site.data;

import junit.framework.TestCase;
import org.dulab.site.models.Peak;
import org.dulab.site.models.Spectrum;

import java.util.Arrays;

public class DefaultSpectrumMatchRepositoryTest extends TestCase {

    public void testMatch() {

        Peak peak1 = new Peak();
        peak1.setMz(100.0);
        peak1.setIntensity(100.0);

        Peak peak2 = new Peak();
        peak2.setMz(110.0);
        peak2.setIntensity(200.0);

        Spectrum spectrum = new Spectrum();
        spectrum.setPeaks(Arrays.asList(peak1, peak2));

        new DefaultSpectrumMatchRepository()
                .match(spectrum);
    }
}