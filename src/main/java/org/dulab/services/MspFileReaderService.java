package org.dulab.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.models.Spectrum;
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

    private void addProperty(Map<String, String> properties, String line) {
        for (String s : line.split(";")) {
            String[] nameValuePair = s.split(":");
            if (nameValuePair.length == 2)
                properties.put(nameValuePair[0].trim(), nameValuePair[1].trim());
        }
    }

    private void addPeak(Map<Double, Double> peaks, String line) {
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

    private void addSpectrum(List<Spectrum> spectra,
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
