package org.dulab.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Spectrum {

    private static final Logger LOG = LogManager.getLogger();

    private static final String NO_PEAKS_MSG = "Attempt to create spectrum with no peaks";
    private static final String NO_PROPERTIES_MSG = "Spectrum contains no properties";

    private final double[] mzValues;
    private final double[] intensities;
    private final Map<String, String> properties;

    public Spectrum(double[] mzValues, double[] intensities, Map<String, String> properties)
            throws IllegalArgumentException {

        try {
            this.mzValues = Objects.requireNonNull(mzValues, NO_PEAKS_MSG);
            this.intensities = Objects.requireNonNull(intensities, NO_PEAKS_MSG);
            this.properties = Objects.requireNonNull(properties, NO_PROPERTIES_MSG);
        }
        catch (IllegalArgumentException e) {
            LOG.error(e);
            throw e;
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
    }

    public Optional<String> getProperty(String propertyName) {
        return Optional.ofNullable(
                properties.get(propertyName));
    }
}
