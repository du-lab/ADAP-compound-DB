package org.dulab.models.readers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.models.Spectrum;

import java.io.*;
import java.util.*;

public class MspReader {

    private static final Logger LOG = LogManager.getLogger();

    public static List<Spectrum> read(InputStream inputStream)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(inputStream, "Input stream is empty")));

        List<Spectrum> spectra = new ArrayList<>();

        Map<String, String> properties = new HashMap<>();
        Map<Double, Double> peaks = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty())
                addSpectrum(spectra, properties, peaks);
            else if (line.contains(":"))
                addProperty(properties, line);
            else
                addPeak(peaks, line);
        }
        addSpectrum(spectra, properties, peaks);

        reader.close();

        return spectra;
    }

    private static void addProperty(Map<String, String> properties, String line) {
        for (String s : line.split(";")) {
            String[] nameValuePair = s.split(":");
            if (nameValuePair.length == 2)
                properties.put(nameValuePair[0].trim(), nameValuePair[1].trim());
        }
    }

    private static void addPeak(Map<Double, Double> peaks, String line) {
        for (String s : line.split(";")) {
            String[] mzIntensityPair = s.split(" ");
            if (mzIntensityPair.length == 2) {
                try {
                    Double mz = Double.valueOf(mzIntensityPair[0]);
                    Double intensity = Double.valueOf(mzIntensityPair[1]);
                    peaks.put(mz, intensity);
                }
                catch (NumberFormatException e) {
                    LOG.warn("Wrong format of mz-intensity pair: " + mzIntensityPair[0] + ", " + mzIntensityPair[1]);
                }
            }
        }
    }

    private static void addSpectrum(List<Spectrum> spectra,
                                    Map<String, String> properties,
                                    Map<Double, Double> peaks) {

        if (peaks.isEmpty() || properties.isEmpty()) {
            LOG.warn("Attempt to save a spectrum with zero peaks");
            return;
        }

        double[] mzValues = peaks.entrySet().stream()
                .mapToDouble(Map.Entry::getKey)
                .toArray();

        double[] intensities = peaks.entrySet().stream()
                .mapToDouble(Map.Entry::getValue)
                .toArray();

        spectra.add(new Spectrum(mzValues, intensities, new HashMap<>(properties)));
        peaks.clear();
        properties.clear();
    }
}
