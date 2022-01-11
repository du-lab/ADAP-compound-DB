package org.dulab.adapcompounddb.site.controllers.forms;

import org.dulab.adapcompounddb.site.services.search.SearchParameters.RetIndexMatchType;
import org.dulab.adapcompounddb.site.services.search.SearchParameters.MzToleranceType;

import java.math.BigInteger;
import java.util.Set;

public class FilterForm {

    private Integer scoreThreshold;
    private Integer retentionIndexTolerance;
    private RetIndexMatchType retentionIndexMatch = RetIndexMatchType.IGNORE_MATCH;
    private Double mzTolerance;
    private MzToleranceType mzToleranceType = MzToleranceType.DA;
    private Integer limit = 10;
    private String species;
    private String source;
    private String disease;
    private Set<BigInteger> submissionIds;
    private boolean withOntologyLevels;
    private boolean sendResultsToEmail;


    public Integer getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(Integer scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    public Integer getRetentionIndexTolerance() {
        return retentionIndexTolerance;
    }

    public void setRetentionIndexTolerance(Integer retentionIndexTolerance) {
        this.retentionIndexTolerance = retentionIndexTolerance;
    }

    public RetIndexMatchType getRetentionIndexMatch() {
        return retentionIndexMatch;
    }

    public void setRetentionIndexMatch(RetIndexMatchType retentionIndexMatch) {
        this.retentionIndexMatch = retentionIndexMatch;
    }

    public Double getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(Double mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public MzToleranceType getMzToleranceType() {
        return mzToleranceType;
    }

    public void setMzToleranceType(MzToleranceType mzToleranceType) {
        this.mzToleranceType = mzToleranceType;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
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

    public boolean isSendResultsToEmail() {
        return sendResultsToEmail;
    }

    public void setSendResultsToEmail(boolean sendResultsToEmail) {
        this.sendResultsToEmail = sendResultsToEmail;
    }
}
