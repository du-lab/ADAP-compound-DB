package org.dulab.adapcompounddb.site.services.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.entities.Adduct;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.*;

public class SearchParameters implements Cloneable {

    private static final Logger LOGGER = LogManager.getLogger(SearchParameters.class);

    public enum RetIndexMatchType {
        IGNORE_MATCH, PENALIZE_NO_MATCH_STRONG, PENALIZE_NO_MATCH_AVERAGE,
        PENALIZE_NO_MATCH_WEAK, ALWAYS_MATCH
    }

    public static final double DEFAULT_MZ_TOLERANCE = 0.01;
    public static final double DEFAULT_SCORE_THRESHOLD = 0.5;

    public enum MzToleranceType {DA, PPM}

    private Boolean greedy;
    private Double scoreThreshold;
    private Double mzTolerance;
    private Integer mzTolerancePPM;
    private Double precursorTolerance;
    private Integer precursorTolerancePPM;
    private Double massTolerance;
    private Integer massTolerancePPM;
    private Double retTimeTolerance;
    private Double retIndexTolerance;
    private RetIndexMatchType retIndexMatchType;
    private Double isotopicSimilarityThreshold;
    private String species;
    private String source;
    private String disease;
    private Set<BigInteger> submissionIds;
    private Iterable<BigInteger> spectrumIds;


    private String Identifier;
//    private double[] masses;
//    private SortedMap<Double, String> massToAdductMap;
    private List<Adduct> adducts;
    private int limit = 100;
    private boolean penalizeQueryImpurities = true;
    private boolean penalizeDominantPeak = true;

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

    public SearchParameters setMzTolerance(Double mzTolerance, Integer mzTolerancePPM) {
        this.mzTolerance = mzTolerance;
        this.mzTolerancePPM = mzTolerancePPM;
        return this;
    }

    public SearchParameters setMzTolerance(Double mzTolerance, MzToleranceType mzToleranceType) {
        switch (mzToleranceType) {
            case PPM:
                setMzTolerancePPM(mzTolerance != null ? mzTolerance.intValue() : null);
                break;
            case DA:
                setMzTolerance(mzTolerance);
                break;
        }
        return this;
    }

    public Integer getMzTolerancePPM() {
        return mzTolerancePPM;
    }

    public void setMzTolerancePPM(Integer mzTolerancePPM) {
        this.mzTolerancePPM = mzTolerancePPM;
    }

    public Double getPrecursorTolerance() {
        return precursorTolerance;
    }

    public SearchParameters setPrecursorTolerance(Double precursorTolerance) {
        this.precursorTolerance = precursorTolerance;
        return this;
    }

    public SearchParameters setPrecursorTolerance(Double tolerance, Integer ppm) {
        this.precursorTolerance = tolerance;
        this.precursorTolerancePPM = ppm;
        return this;
    }

    public Integer getPrecursorTolerancePPM() {
        return precursorTolerancePPM;
    }

    public SearchParameters setPrecursorTolerancePPM(Integer precursorTolerancePPM) {
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

    public SearchParameters setMassTolerance(Double tolerance, Integer ppm) {
        this.massTolerance = tolerance;
        this.massTolerancePPM = ppm;
        return this;
    }

    public Integer getMassTolerancePPM() {
        return massTolerancePPM;
    }

    public SearchParameters setMassTolerancePPM(Integer massTolerancePPM) {
        this.massTolerancePPM = massTolerancePPM;
        return this;
    }

//    public double[] getMasses() {
//        return masses;
//    }
//
//    public SearchParameters setMasses(double[] masses) {
//        this.masses = masses;
//        return this;
//    }


//    public Map<Double, String> getMassToAdductMap() {
//        return massToAdductMap;
//    }
//
//    public SearchParameters setMassToAdductMap(Map<Double, String> massToAdductMap) {
//        this.massToAdductMap = massToAdductMap;
//        return this;
//    }


    public List<Adduct> getAdducts() {
        return adducts;
    }

    public SearchParameters setAdducts(List<Adduct> adducts) {
        this.adducts = adducts;
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
                parameters.setMzTolerance(DEFAULT_MZ_TOLERANCE);
                parameters.setScoreThreshold(DEFAULT_SCORE_THRESHOLD);
                break;
            case LC_MSMS_POS:
            case LC_MSMS_NEG:
                parameters.setMzTolerance(DEFAULT_MZ_TOLERANCE);
                parameters.setScoreThreshold(0.3);
                parameters.setPrecursorTolerance(DEFAULT_MZ_TOLERANCE);
                break;
            case NONE:
                parameters.setMassTolerance(DEFAULT_MZ_TOLERANCE);
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

    public boolean isPenalizeQueryImpurities() {
        return penalizeQueryImpurities;
    }

    public void setPenalizeQueryImpurities(boolean penalizeQueryImpurities) {
        this.penalizeQueryImpurities = penalizeQueryImpurities;
    }

    public boolean isPenalizeDominantPeak() {
        return penalizeDominantPeak;
    }

    public void setPenalizeDominantPeak(boolean penalizeDominantPeak) {
        this.penalizeDominantPeak = penalizeDominantPeak;
    }

    public String getIdentifier() {
        return Identifier;
    }

    public void setIdentifier(String identifier) {
        Identifier = identifier;
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
        parameters.setAdducts(this.getAdducts() != null ? new ArrayList<>(this.getAdducts()) : null);
        if (this.getSpectrumIds() != null) {
            List<BigInteger> spectrumIds = new ArrayList<>();
            this.getSpectrumIds().forEach(spectrumIds::add);
            parameters.setSpectrumIds(spectrumIds);
        }
        return parameters;
    }
}
