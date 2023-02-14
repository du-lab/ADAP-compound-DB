package org.dulab.adapcompounddb.models.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
@NoArgsConstructor
@AllArgsConstructor
public class SearchParametersDTO {

    private Integer scoreThreshold = 1;

    private Double retentionIndexTolerance = 50.0;

    private SearchParameters.RetIndexMatchType retentionIndexMatch = SearchParameters.RetIndexMatchType.IGNORE_MATCH;

    private Double mzTolerance = 0.01;

    private Integer limit = 100;

    private SearchParameters.MzToleranceType mzToleranceType = SearchParameters.MzToleranceType.DA;

    private boolean customParameters = false;

    public void checkCustomParameters() {
        if (this.scoreThreshold != 1
                || this.retentionIndexTolerance != 50.0
                || this.retentionIndexMatch != SearchParameters.RetIndexMatchType.IGNORE_MATCH
                || this.mzTolerance != 0.01
                || this.limit != 100
                || this.mzToleranceType != SearchParameters.MzToleranceType.DA) {
            customParameters = true;
        }
    }

    public boolean isCustomParameters() {
        return this.customParameters;
    }

    public Integer getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(Integer scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    public Double getRetentionIndexTolerance() {
        return retentionIndexTolerance;
    }

    public void setRetentionIndexTolerance(Double retentionIndexTolerance) {
        this.retentionIndexTolerance = retentionIndexTolerance;
    }

    public SearchParameters.RetIndexMatchType getRetentionIndexMatch() {
        return retentionIndexMatch;
    }

    public void setRetentionIndexMatch(SearchParameters.RetIndexMatchType retentionIndexMatch) {
        this.retentionIndexMatch = retentionIndexMatch;
    }

    public Double getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(Double mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public SearchParameters.MzToleranceType getMzToleranceType() {
        return mzToleranceType;
    }

    public void setMzToleranceType(SearchParameters.MzToleranceType mzToleranceType) {
        this.mzToleranceType = mzToleranceType;
    }

    public void setCustomParameters(boolean customParameters) {
        this.customParameters = customParameters;
    }
}
