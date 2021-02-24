package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import java.util.Set;

public class SearchParameters {

    private Double scoreThreshold;
    private Double mzTolerance;
    private Double precursorTolerance;
    private Double massTolerance;
    private Double retTimeTolerance;
    private String species;
    private String source;
    private String disease;
    private Set<Long> submissionIds;

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

    public Double getPrecursorTolerance() {
        return precursorTolerance;
    }

    public SearchParameters setPrecursorTolerance(Double precursorTolerance) {
        this.precursorTolerance = precursorTolerance;
        return this;
    }

    public Double getMassTolerance() {
        return massTolerance;
    }

    public SearchParameters setMassTolerance(Double massTolerance) {
        this.massTolerance = massTolerance;
        return this;
    }

    public Double getRetTimeTolerance() {
        return retTimeTolerance;
    }

    public SearchParameters setRetTimeTolerance(Double retTimeTolerance) {
        this.retTimeTolerance = retTimeTolerance;
        return this;
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

    public Set<Long> getSubmissionIds() {
        return submissionIds;
    }

    public SearchParameters setSubmissionIds(Set<Long> submissionIds) {
        this.submissionIds = submissionIds;
        return this;
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
                parameters.setMzTolerance(0.001);
                parameters.setScoreThreshold(0.3);
                parameters.setPrecursorTolerance(0.01);
                break;
            case NONE:
                parameters.setMassTolerance(0.01);
                break;
        }
        return parameters;
    }
}
