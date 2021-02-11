package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import java.util.Set;

public class SearchParameters {

    private Double scoreThreshold;
    private Double mzTolerance;
    private Double precursorTolerance;
    private Double molecularWeightTolerance;
    private String species;
    private String source;
    private String disease;
    private Set<Long> submissionIds;

    public Double getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(Double scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    public Double getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(Double mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public Double getPrecursorTolerance() {
        return precursorTolerance;
    }

    public void setPrecursorTolerance(Double precursorTolerance) {
        this.precursorTolerance = precursorTolerance;
    }

    public Double getMolecularWeightTolerance() {
        return molecularWeightTolerance;
    }

    public void setMolecularWeightTolerance(Double molecularWeightTolerance) {
        this.molecularWeightTolerance = molecularWeightTolerance;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Set<Long> getSubmissionIds() {
        return submissionIds;
    }

    public void setSubmissionIds(Set<Long> submissionIds) {
        this.submissionIds = submissionIds;
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
                parameters.setMolecularWeightTolerance(0.01);
                break;
        }
        return parameters;
    }
}
