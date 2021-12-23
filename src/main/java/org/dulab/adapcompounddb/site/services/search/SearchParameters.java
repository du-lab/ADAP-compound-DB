package org.dulab.adapcompounddb.site.services.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchParameters implements Cloneable {

    private static final Logger LOGGER = LogManager.getLogger(SearchParameters.class);

    public enum RetIndexMatchType {
        IGNORE_MATCH, PENALIZE_NO_MATCH_STRONG, PENALIZE_NO_MATCH_AVERAGE,
        PENALIZE_NO_MATCH_WEAK, ALWAYS_MATCH
    }

    public enum MzToleranceType {DA, PPM}

    private Boolean greedy;
    private Double scoreThreshold;
    private Double mzTolerance;
    private Double mzTolerancePPM;
    private Double precursorTolerance;
    private Double precursorTolerancePPM;
    private Double massTolerance;
    private Double massTolerancePPM;
    private Double retTimeTolerance;
    private Double retIndexTolerance;
    private RetIndexMatchType retIndexMatchType;
    private Double isotopicSimilarityThreshold;
    private String species;
    private String source;
    private String disease;
    private Set<BigInteger> submissionIds;
    private Iterable<BigInteger> spectrumIds;
    private double[] masses;
    private int limit = 100;

    public Boolean getGreedy() {
        return greedy;
    }

    public void setGreedy(Boolean greedy) {
        this.greedy = greedy;
    }

    public Double getScoreThreshold() {
        return scoreThreshold;
    }

    public SearchParameters setScoreThreshold(Double scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
        return this;
    }

    public Double getMzTolerance() {
        return mzTolerance;
    }

    public SearchParameters setMzTolerance(Double mzTolerance) {
        this.mzTolerance = mzTolerance;
        return this;
    }

    public SearchParameters setMzTolerance(Double mzTolerance, Double mzTolerancePPM) {
        this.mzTolerance = mzTolerance;
        this.mzTolerancePPM = mzTolerancePPM;
        return this;
    }

    public SearchParameters setMzTolerance(Double mzTolerance, MzToleranceType mzToleranceType) {
        switch (mzToleranceType) {
            case PPM:
                setMzTolerancePPM(mzTolerance);
                break;
            case DA:
                setMzTolerance(mzTolerance);
                break;
        }
        return this;
    }

    public Double getMzTolerancePPM() {
        return mzTolerancePPM;
    }

    public void setMzTolerancePPM(Double mzTolerancePPM) {
        this.mzTolerancePPM = mzTolerancePPM;
    }

    public Double getPrecursorTolerance() {
        return precursorTolerance;
    }

    public SearchParameters setPrecursorTolerance(Double precursorTolerance) {
        this.precursorTolerance = precursorTolerance;
        return this;
    }

    public SearchParameters setPrecursorTolerance(Double tolerance, Double ppm) {
        this.precursorTolerance = tolerance;
        this.precursorTolerancePPM = ppm;
        return this;
    }

    public Double getPrecursorTolerancePPM() {
        return precursorTolerancePPM;
    }

    public SearchParameters setPrecursorTolerancePPM(Double precursorTolerancePPM) {
        this.precursorTolerancePPM = precursorTolerancePPM;
        return this;
    }

    public Double getMassTolerance() {
        return massTolerance;
    }

    public SearchParameters setMassTolerance(Double massTolerance) {
        this.massTolerance = massTolerance;
        return this;
    }

    public SearchParameters setMassTolerance(Double tolerance, Double ppm) {
        this.massTolerance = tolerance;
        this.massTolerancePPM = ppm;
        return this;
    }

    public Double getMassTolerancePPM() {
        return massTolerancePPM;
    }

    public SearchParameters setMassTolerancePPM(Double massTolerancePPM) {
        this.massTolerancePPM = massTolerancePPM;
        return this;
    }

    public double[] getMasses() {
        return masses;
    }

    public SearchParameters setMasses(double[] masses) {
        this.masses = masses;
        return this;
    }

    public Double getRetTimeTolerance() {
        return retTimeTolerance;
    }

    public SearchParameters setRetTimeTolerance(Double retTimeTolerance) {
        this.retTimeTolerance = retTimeTolerance;
        return this;
    }

    public Double getRetIndexTolerance() {
        return retIndexTolerance;
    }

    public void setRetIndexTolerance(Double retIndexTolerance) {
        this.retIndexTolerance = retIndexTolerance;
    }

    public RetIndexMatchType getRetIndexMatchType() {
        return retIndexMatchType;
    }

    public void setRetIndexMatchType(RetIndexMatchType retIndexMatchType) {
        this.retIndexMatchType = retIndexMatchType;
    }

    public Double getIsotopicSimilarityThreshold() {
        return isotopicSimilarityThreshold;
    }

    public void setIsotopicSimilarityThreshold(Double isotopicSimilarityThreshold) {
        this.isotopicSimilarityThreshold = isotopicSimilarityThreshold;
    }

    public String getSpecies() {
        return species;
    }

    public SearchParameters setSpecies(String species) {
        this.species = species;
        return this;
    }

    public String getSource() {
        return source;
    }

    public SearchParameters setSource(String source) {
        this.source = source;
        return this;
    }

    public String getDisease() {
        return disease;
    }

    public SearchParameters setDisease(String disease) {
        this.disease = disease;
        return this;
    }

    public Set<BigInteger> getSubmissionIds() {
        return submissionIds;
    }

    public SearchParameters setSubmissionIds(Set<BigInteger> submissionIds) {
        this.submissionIds = submissionIds;
        return this;
    }

    public Iterable<BigInteger> getSpectrumIds() {
        return spectrumIds;
    }

    public void setSpectrumIds(Iterable<BigInteger> spectrumIds) {
        this.spectrumIds = spectrumIds;
    }

    public static SearchParameters getDefaultParameters(ChromatographyType type) {
        SearchParameters parameters = new SearchParameters();
        switch (type) {
            case GAS:
            case LIQUID_POSITIVE:
            case LIQUID_NEGATIVE:
                parameters.setMzTolerance(0.01);
                parameters.setScoreThreshold(0.5);
                break;
            case LC_MSMS_POS:
            case LC_MSMS_NEG:
                parameters.setMzTolerancePPM(0.001);
                parameters.setScoreThreshold(0.3);
                parameters.setPrecursorTolerance(0.01);
                break;
            case NONE:
                parameters.setMassTolerance(0.01);
                break;
        }
        return parameters;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Sets variables of the current instance to the non-null values of 'other' instance
     *
     * @param other instance of `SearchParameters`
     * @return updated current instance
     */
    public SearchParameters merge(SearchParameters other) {
        if (other == null) return this;
        for (Field field : other.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            try {
                Object value = field.get(other);
                if (value != null) {
                    this.getClass().getDeclaredField(field.getName()).set(this, value);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                LOGGER.warn("Error when merging two search parameters: " + e.getMessage(), e);
            }
        }
        return this;
    }

    @Override
    public SearchParameters clone() throws CloneNotSupportedException {
        SearchParameters parameters = (SearchParameters) super.clone();
        parameters.setSubmissionIds(new HashSet<>(this.getSubmissionIds()));
        parameters.setMasses(this.getMasses() != null ? this.getMasses().clone() : null);
        if (this.getSpectrumIds() != null) {
            List<BigInteger> spectrumIds = new ArrayList<>();
            this.getSpectrumIds().forEach(spectrumIds::add);
            parameters.setSpectrumIds(spectrumIds);
        }
        return parameters;
    }
}
