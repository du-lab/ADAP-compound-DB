package org.dulab.adapcompounddb.models;

import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class QueryParameters {

    private Double retTimeTolerance = null;
    private Double precursorTolerance = null;
    private Double mzTolerance = null;
    private Double scoreThreshold = null;
    private Set<Spectrum> excludeSpectra = null;

    public Double getRetTimeTolerance() {
        return retTimeTolerance;
    }

    public void setRetTimeTolerance(Double retTimeTolerance) {
        this.retTimeTolerance = retTimeTolerance;
    }

    public Double getPrecursorTolerance() {
        return precursorTolerance;
    }

    public void setPrecursorTolerance(Double precursorTolerance) {
        this.precursorTolerance = precursorTolerance;
    }

    public Double getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(Double mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public Double getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(Double scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    public Set<Spectrum> getExcludeSpectra() {
        return excludeSpectra;
    }

    public void setExcludeSpectra(Set<Spectrum> excludeSpectra) {
        this.excludeSpectra = excludeSpectra;
    }

    public void addExludeSpectra(Set<Spectrum> excludeSpectra) {
        if (this.excludeSpectra == null)
            this.excludeSpectra = excludeSpectra;
        else
            this.excludeSpectra.addAll(excludeSpectra);
    }

    public static QueryParameters getDefault() {
        QueryParameters params = new QueryParameters();
        params.setRetTimeTolerance(0.5);
        params.setPrecursorTolerance(0.01);
        params.setScoreThreshold(0.75);
        params.setMzTolerance(0.01);
        params.setExcludeSpectra(null);
        return params;
    }
}
