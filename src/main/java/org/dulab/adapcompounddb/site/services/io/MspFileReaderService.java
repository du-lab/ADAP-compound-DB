package org.dulab.adapcompounddb.site.services.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumProperty;
import org.springframework.lang.Nullable;
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
    public List<Spectrum> read(InputStream inputStream, @Nullable MetaDataMapping mapping)
            throws IOException {

        mapping = validateMetaDataMapping(mapping);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(inputStream, "Input stream is empty")));

        List<Spectrum> spectra = new ArrayList<>();
        Spectrum spectrum = new Spectrum();
        List<Peak> peaks = new ArrayList<>();
        List<SpectrumProperty> properties = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                if (!peaks.isEmpty() || !properties.isEmpty()) {
                    spectrum.setPeaks(peaks, true);
                    spectrum.setProperties(properties, mapping);
                    spectra.add(spectrum);
                }
                spectrum = new Spectrum();
                peaks = new ArrayList<>();
                properties = new ArrayList<>();
            } else if (line.contains(":")) {
                // Add property
                String[] nameValuePair = line.split(":", 2);
                if (nameValuePair.length == 2) {
                    SpectrumProperty property = new SpectrumProperty();
                    property.setName(nameValuePair[0].trim());
                    property.setValue(nameValuePair[1].trim());
                    property.setSpectrum(spectrum);
                    properties.add(property);
                }
            } else
                addPeak(spectrum, peaks, line);
        }

        if (!peaks.isEmpty()) {
            spectrum.setPeaks(peaks, true);
            spectrum.setProperties(properties, mapping);
            spectra.add(spectrum);
        }

        reader.close();

        return spectra;
    }

    private void addPeak(Spectrum spectrum, List<Peak> peaks, String line) {

        for (String s : line.split(";")) {
            String[] mzIntensityPair = s.split("[ \t]+");  // Split by any combination of the blank space and tab characters
            if (mzIntensityPair.length == 2) {
                try {
                    Peak peak = new Peak();
                    peak.setMz(Double.parseDouble(mzIntensityPair[0]));
                    peak.setIntensity(Double.parseDouble(mzIntensityPair[1]));
                    peak.setSpectrum(spectrum);
                    peaks.add(peak);
                } catch (NumberFormatException e) {
                    LOG.warn("Wrong format of mz-intensity pair: " + mzIntensityPair[0] + ", " + mzIntensityPair[1]);
                }
            }
        }
    }

    @Override
    public MetaDataMapping validateMetaDataMapping(MetaDataMapping mapping) {
        if (mapping == null)
            mapping = new MetaDataMapping();
        if (mapping.getNameField() == null || mapping.getNameField().isEmpty())
            mapping.setNameField("Name");
        if (mapping.getPrecursorMzField() == null || mapping.getPrecursorMzField().isEmpty())
            mapping.setPrecursorMzField("PrecursorMZ");
        if (mapping.getPrecursorTypeField() == null || mapping.getPrecursorTypeField().isEmpty())
            mapping.setPrecursorTypeField("Precursor_type");
        if (mapping.getFormulaField() == null || mapping.getFormulaField().isEmpty())
            mapping.setFormulaField("Formula");
        return mapping;
    }
}
