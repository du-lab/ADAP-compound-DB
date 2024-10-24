package org.dulab.adapcompounddb.site.services.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.models.MetaDataMapping.Field;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.dto.SpectrumProperty;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MspFileReaderService implements FileReaderService {

    private static final Logger LOG = LoggerFactory.getLogger(MspFileReaderService.class);
    private static final Pattern PEAK_PATTERN = Pattern.compile("([0-9]+[:\\s][0-9]+[;\\s]?)+");

    private boolean roundMzValues = false;

    @Override
    public List<Spectrum> read(InputStream inputStream, @Nullable MetaDataMapping mapping, String filename,
                               ChromatographyType chromatographyType)
            throws IOException {

        mapping = validateMetaDataMapping(mapping);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(inputStream, "Input stream is empty")));

        List<Spectrum> spectra = new ArrayList<>();
        Spectrum spectrum = new Spectrum();
        List<Peak> peaks = new ArrayList<>();
        Map<String, SpectrumProperty> properties = new HashMap<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                if (!peaks.isEmpty() || !properties.isEmpty()) {
                    spectrum.setPeaks(peaks, true);
                    spectrum.setProperties(new ArrayList<>(properties.values()), mapping);
                    spectra.add(spectrum);
                }
                spectrum = new Spectrum();
                peaks = new ArrayList<>();
                properties = new HashMap<>();

            } else if (!line.contains(":") || PEAK_PATTERN.matcher(line).matches()) {
                addPeak(spectrum, peaks, line);

            } else if (line.contains(":")) {
                // Add property
                String[] nameValuePair = line.split(":");  // We can have more than two name-value pairs if there are multiple separators
                if (nameValuePair.length >= 2) {
                    nameValuePair = validateNameValuePair(nameValuePair, mapping);
                    if (nameValuePair != null) {
                        SpectrumProperty property = new SpectrumProperty();
                        property.setName(nameValuePair[0].trim());
                        property.setValue(nameValuePair[1].trim());
                        property.setSpectrum(spectrum);
                        properties.putIfAbsent(property.getName(), property);
//                        properties.add(property);
                    }
                }
            } else
                addPeak(spectrum, peaks, line);
        }

        if (!peaks.isEmpty()) {
            spectrum.setPeaks(peaks, true);
            spectrum.setProperties(new ArrayList<>(properties.values()), mapping);
            spectra.add(spectrum);
        }

        reader.close();

        return spectra;
    }

    public boolean isRoundMzValues() {
        return roundMzValues;
    }

    public void setRoundMzValues(boolean roundMzValues) {
        this.roundMzValues = roundMzValues;
    }

    private void addPeak(Spectrum spectrum, List<Peak> peaks, String line) {

        line = line.replaceAll("\".+\"", "");
        String[] pairs = line.split("[:;\\s()]+");
        pairs = Arrays.stream(pairs)
                .filter(s -> s.length() > 0)
                .toArray(String[]::new);
        for (int i = 0; i < pairs.length; i += 2) {
            try {
                Peak peak = new Peak();
                double mz = Double.parseDouble(pairs[i]);
                if (roundMzValues)
                    mz = Math.round(mz);
                peak.setMz(mz);
                peak.setIntensity(Double.parseDouble(pairs[i + 1]));
                peak.setSpectrum(spectrum);
                peaks.add(peak);

            } catch (NumberFormatException e) {
                LOG.warn("Wrong format of mz-intensity pair: " + line);
            }
        }
//        for (String s : line.split(";")) {
//            String[] mzIntensityPair = s.split("[ \t]+");  // Split by any combination of the blank space and tab characters
//            if (mzIntensityPair.length >= 2) {
//                try {
//                    Peak peak = new Peak();
//                    peak.setMz(Double.parseDouble(mzIntensityPair[0]));
//                    peak.setIntensity(Double.parseDouble(mzIntensityPair[1]));
//                    peak.setSpectrum(spectrum);
//                    peaks.add(peak);
//                } catch (NumberFormatException e) {
//                    LOG.warn("Wrong format of mz-intensity pair: " + mzIntensityPair[0] + ", " + mzIntensityPair[1]);
//                }
//            }
//        }
    }

    private static String[] validateNameValuePair(String[] nameValuePair, MetaDataMapping metaDataMapping) {
        String fieldName = null;
        String fieldValue = null;
        for (int i = 0; i < nameValuePair.length - 1; ++i) {
            fieldName = nameValuePair[i].trim();
            fieldValue = Arrays.stream(nameValuePair, i + 1, nameValuePair.length)
                    .collect(Collectors.joining(": ")).trim();
            if (metaDataMapping.check(fieldName) || !fieldName.equalsIgnoreCase("Synon")) {
                break;
            }
        }
        return (fieldName != null) ? new String[]{fieldName, fieldValue} : null;
    }

    @Override
    public MetaDataMapping validateMetaDataMapping(MetaDataMapping mapping) {
        if (mapping == null)
            mapping = new MetaDataMapping();

        String nameField = mapping.getFieldName(Field.NAME);
        if (nameField == null || nameField.isEmpty())
            mapping.setFieldName(Field.NAME, "Name");

        String precursorMzField = mapping.getFieldName(Field.PRECURSOR_MZ);
        if (precursorMzField == null || precursorMzField.isEmpty())
            mapping.setFieldName(Field.PRECURSOR_MZ, "PrecursorMZ");

        String precursorTypeField = mapping.getFieldName(Field.PRECURSOR_TYPE);
        if (precursorTypeField == null || precursorTypeField.isEmpty())
            mapping.setFieldName(Field.PRECURSOR_TYPE, "Precursor_type");

        String formulaField = mapping.getFieldName(Field.FORMULA);
        if (formulaField == null || formulaField.isEmpty())
            mapping.setFieldName(Field.FORMULA, "Formula");

        return mapping;
    }
}
