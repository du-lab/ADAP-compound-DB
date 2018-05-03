package org.dulab.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.models.entities.Peak;
import org.dulab.models.entities.Spectrum;
import org.dulab.models.entities.SpectrumProperty;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class MspFileReaderService implements FileReaderService {

    private static final Logger LOG = LogManager.getLogger();

    @Override
    public List<Spectrum> read(InputStream inputStream)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(inputStream, "Input stream is empty")));

        List<Spectrum> spectra = new ArrayList<>();
        Spectrum spectrum = new Spectrum();
        List<Peak> peaks = new ArrayList<>();
        List<SpectrumProperty> properties = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {

                spectrum.setProperties(properties);
                spectrum.setPeaks(peaks);
                spectra.add(spectrum);

                spectrum = new Spectrum();
                properties = new ArrayList<>();
                peaks = new ArrayList<>();
            }
            else if (line.contains(":"))
                addProperty(spectrum, properties, line);
            else
                addPeak(spectrum, peaks, line);
        }

        if (!peaks.isEmpty()) {
            spectrum.setProperties(properties);
            spectrum.setPeaks(peaks);
            spectra.add(spectrum);
        }

        reader.close();

        return spectra;
    }

    private void addProperty(Spectrum spectrum, List<SpectrumProperty> properties, String line) {

        for (String s : line.split(";")) {
            String[] nameValuePair = s.split(":");
            if (nameValuePair.length == 2) {
                SpectrumProperty property = new SpectrumProperty();
                property.setName(nameValuePair[0].trim());
                property.setValue(nameValuePair[1].trim());
                property.setSpectrum(spectrum);
                properties.add(property);
            }
        }
    }

    private void addPeak(Spectrum spectrum, List<Peak> peaks, String line) {

        for (String s : line.split(";")) {
            String[] mzIntensityPair = s.split(" ");
            if (mzIntensityPair.length == 2) {
                try {
                    Peak peak = new Peak();
                    peak.setMz(Double.valueOf(mzIntensityPair[0]));
                    peak.setIntensity(Double.valueOf(mzIntensityPair[1]));
                    peak.setSpectrum(spectrum);
                    peaks.add(peak);
                }
                catch (NumberFormatException e) {
                    LOG.warn("Wrong format of mz-intensity pair: " + mzIntensityPair[0] + ", " + mzIntensityPair[1]);
                }
            }
        }
    }

//    private void addSpectrum(List<Spectrum> spectra,
//                             List<SpectrumProperty> properties,
//                             List<Peak> peaks) {
//
//        if (peaks.isEmpty() || properties.isEmpty()) {
//            LOG.warn("Attempt to save a spectrum with zero peaks");
//            return;
//        }
//
//        Spectrum spectrum = new Spectrum();
//        spectrum.setProperties(properties);
//        spectrum.setPeaks(peaks);
//        spectra.add(spectrum);
//    }
}
