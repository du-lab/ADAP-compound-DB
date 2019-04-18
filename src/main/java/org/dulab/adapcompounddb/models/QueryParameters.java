package org.dulab.adapcompounddb.models;

import java.util.HashSet;
import java.util.Set;

public class QueryParameters {

    private Double retTimeTolerance = null;
    private Double precursorTolerance = null;
    private Double mzTolerance = null;
    private Double scoreThreshold = null;
    private Set<String> tags = null;
    private Set<Long> excludeSpectra = null;

    public Double getRetTimeTolerance() {
        return retTimeTolerance;
    }

    public QueryParameters setRetTimeTolerance(final Double retTimeTolerance) {
        this.retTimeTolerance = retTimeTolerance;
        return this;
    }

    public Double getPrecursorTolerance() {
        return precursorTolerance;
    }

    public QueryParameters setPrecursorTolerance(final Double precursorTolerance) {
        this.precursorTolerance = precursorTolerance;
        return this;
    }

    public Double getMzTolerance() {
        return mzTolerance;
    }

    public QueryParameters setMzTolerance(final Double mzTolerance) {
        this.mzTolerance = mzTolerance;
        return this;
    }

    public Double getScoreThreshold() {
        return scoreThreshold;
    }

    public QueryParameters setScoreThreshold(final Double scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public QueryParameters setTags(final Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public Set<Long> getExcludeSpectra() {
        return excludeSpectra;
    }

    public QueryParameters setExcludeSpectra(final Set<Long> excludeSpectra) {
        this.excludeSpectra = excludeSpectra;
        return this;
    }

    public QueryParameters addExludeSpectra(final Set<Long> excludeSpectra) {
        if (this.excludeSpectra == null) {
            this.excludeSpectra = new HashSet<>(excludeSpectra);
        } else {
            this.excludeSpectra.addAll(excludeSpectra);
        }
        return this;
    }

    public static QueryParameters getDefault() {
        final QueryParameters params = new QueryParameters();
        params.setRetTimeTolerance(0.5);
        params.setPrecursorTolerance(0.01);
        params.setScoreThreshold(0.75);
        params.setMzTolerance(0.01);
        params.setTags(null);
        params.setExcludeSpectra(null);
        return params;
    }
}
