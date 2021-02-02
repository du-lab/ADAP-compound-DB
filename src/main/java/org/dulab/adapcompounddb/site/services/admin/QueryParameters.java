package org.dulab.adapcompounddb.site.services.admin;

import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.util.Set;

public class QueryParameters {

    private Double retTimeTolerance = null;
    private Double precursorTolerance = null;
    private Double mzTolerance = null;
    private Double scoreThreshold = null;
    private Double molecularWeightThreshold = null;
    private Set<String> tags = null;
    private Set<Spectrum> excludeSpectra = null;

    public Double getRetTimeTolerance() {
        return retTimeTolerance;
    }

    public QueryParameters setRetTimeTolerance(Double retTimeTolerance) {
        this.retTimeTolerance = retTimeTolerance;
        return this;
    }

    public Double getPrecursorTolerance() {
        return precursorTolerance;
    }

    public QueryParameters setPrecursorTolerance(Double precursorTolerance) {
        this.precursorTolerance = precursorTolerance;
        return this;
    }

    public Double getMzTolerance() {
        return mzTolerance;
    }

    public QueryParameters setMzTolerance(Double mzTolerance) {
        this.mzTolerance = mzTolerance;
        return this;
    }

    public Double getScoreThreshold() {
        return scoreThreshold;
    }

    public QueryParameters setScoreThreshold(Double scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
        return this;
    }

    public Double getMolecularWeightThreshold() {
        return molecularWeightThreshold;
    }

    public QueryParameters setMolecularWeightThreshold(Double molecularWeightThreshold) {
        this.molecularWeightThreshold = molecularWeightThreshold;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public QueryParameters setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public Set<Spectrum> getExcludeSpectra() {
        return excludeSpectra;
    }

    public QueryParameters setExcludeSpectra(Set<Spectrum> excludeSpectra) {
        this.excludeSpectra = excludeSpectra;
        return this;
    }

    public QueryParameters addExludeSpectra(Set<Spectrum> excludeSpectra) {
        if (this.excludeSpectra == null)
            this.excludeSpectra = excludeSpectra;
        else
            this.excludeSpectra.addAll(excludeSpectra);
        return this;
    }

    public static QueryParameters getDefault() {
        QueryParameters params = new QueryParameters();
        params.setRetTimeTolerance(0.5);
        params.setPrecursorTolerance(0.01);
        params.setScoreThreshold(0.75);
        params.setMzTolerance(0.01);
        params.setTags(null);
        params.setExcludeSpectra(null);
        return params;
    }
}
