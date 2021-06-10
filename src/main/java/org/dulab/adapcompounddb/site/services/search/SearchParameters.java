package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchParameters implements Cloneable {

    private Boolean greedy;
    private Double scoreThreshold;
    private Double mzTolerance;
    private Double mzTolerancePPM;
    private Double precursorTolerance;
    private Double precursorTolerancePPM;
    private Double massTolerance;
    private Double massTolerancePPM;
    private Double retTimeTolerance;
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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
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
