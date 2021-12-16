package org.dulab.adapcompounddb.site.controllers.forms;

import org.dulab.adapcompounddb.site.services.search.SearchParameters.RetIndexMatchType;
import org.dulab.adapcompounddb.site.services.search.SearchParameters.MzToleranceType;

import java.math.BigInteger;
import java.util.Set;

public class FilterForm {

    private int scoreThreshold = 500;
    private int retentionIndexTolerance = 40;
    private RetIndexMatchType retentionIndexMatch = RetIndexMatchType.PENALIZE_NO_MATCH;
    private double mzTolerance = 0.01;
    private MzToleranceType mzToleranceType = MzToleranceType.DA;
    private int limit = 10;
    private String species;
    private String source;
    private String disease;
    private Set<BigInteger> submissionIds;
    private boolean withOntologyLevels;


    public int getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(int scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    public int getRetentionIndexTolerance() {
        return retentionIndexTolerance;
    }

    public void setRetentionIndexTolerance(int retentionIndexTolerance) {
        this.retentionIndexTolerance = retentionIndexTolerance;
    }

    public RetIndexMatchType getRetentionIndexMatch() {
        return retentionIndexMatch;
    }

    public void setRetentionIndexMatch(RetIndexMatchType retentionIndexMatch) {
        this.retentionIndexMatch = retentionIndexMatch;
    }

    public double getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(double mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public MzToleranceType getMzToleranceType() {
        return mzToleranceType;
    }

    public void setMzToleranceType(MzToleranceType mzToleranceType) {
        this.mzToleranceType = mzToleranceType;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
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

    public Set<BigInteger> getSubmissionIds() {
        return submissionIds;
    }

    public void setSubmissionIds(Set<BigInteger> submissionIds) {
        this.submissionIds = submissionIds;
    }

    public boolean isWithOntologyLevels() {
        return withOntologyLevels;
    }

    public void setWithOntologyLevels(boolean withOntologyLevels) {
        this.withOntologyLevels = withOntologyLevels;
    }
}
