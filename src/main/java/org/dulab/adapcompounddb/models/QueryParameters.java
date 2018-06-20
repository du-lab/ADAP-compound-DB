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
        return Objects.requireNonNull(retTimeTolerance, "Parameter RetTimeTolerance is not defined.");
    }

    public void setRetTimeTolerance(double retTimeTolerance) {
        this.retTimeTolerance = retTimeTolerance;
    }

    public double getPrecursorTolerance() {
        return Objects.requireNonNull(precursorTolerance, "Parameter PrecursorTolerance is not defined.");
    }

    public void setPrecursorTolerance(double precursorTolerance) {
        this.precursorTolerance = precursorTolerance;
    }

    public double getMzTolerance() {
        return Objects.requireNonNull(mzTolerance, "Parameter MzTolerance is not defined.");
    }

    public void setMzTolerance(double mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public double getScoreThreshold() {
        return Objects.requireNonNull(scoreThreshold, "Parameter ScoreThreshold is not defined.");
    }

    public void setScoreThreshold(double scoreThreshold) {
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
