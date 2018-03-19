package org.dulab.models.readers;

import org.dulab.models.Spectrum;

import java.io.*;
import java.util.*;

public class MspReader {

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

        reader.close();

        return spectra;
    }

}
