package org.dulab.site.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Spectrum {

    private static final Logger LOG = LogManager.getLogger();

    private static final String NO_PEAKS_MSG = "Attempt to create spectrum with no peaks";
    private static final String NO_PROPERTIES_MSG = "Spectrum contains no properties";

    private static final String NAME_PROPERTY = "name";

    private final double[] mzValues;
    private final double[] intensities;
    private final Map<String, String> properties;

    private String name = "UNKNOWN";

    public Spectrum(double[] mzValues, double[] intensities, Map<String, String> properties)
            throws IllegalArgumentException {

        double maxIntensity = Arrays.stream(intensities)
                .max()
                .orElseThrow(IllegalArgumentException::new);

        intensities = Arrays.stream(intensities)
                .map(i -> 100.0 * i / maxIntensity)
                .toArray();

        try {
            this.mzValues = Objects.requireNonNull(mzValues, NO_PEAKS_MSG);
            this.intensities = Objects.requireNonNull(intensities, NO_PEAKS_MSG);
            this.properties = Objects.requireNonNull(properties, NO_PROPERTIES_MSG);
        }
        catch (IllegalArgumentException e) {
            LOG.error(e);
            throw e;
        }

        for (Map.Entry<String, String> e : properties.entrySet())
            if (e.getKey().equalsIgnoreCase(NAME_PROPERTY)) {
                this.name = e.getValue();
                break;
            }
    }

    public Spectrum(double[] mzValues, double[] intensities) {
        this(mzValues, intensities, new HashMap<>());
    }

    public void addProperty(String propertyName, String propertyValue)
            throws IllegalArgumentException {

        try {
            properties.put(
                    Objects.requireNonNull(propertyName, "Property name is empty"),
                    Objects.requireNonNull(propertyValue, "Property value is empty"));
        }
        catch (IllegalArgumentException e) {
            LOG.error(e);
            throw e;
        }

        if (propertyName.equalsIgnoreCase(NAME_PROPERTY))
            name = propertyValue;
    }

    public Optional<String> getProperty(String propertyName) {
        return Optional.ofNullable(
                properties.get(propertyName));
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public double[] getMzValues() {
        return mzValues;
    }

    public double[] getIntensities() {
        return intensities;
    }

    @Override
    public String toString() {
        return name;
    }
}
