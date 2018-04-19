package org.dulab.models;

import java.io.Serializable;

public class UserParameters implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private float spectrumSearchMzTolerance = 0.1F;

    private int spectrumSearchNumHits = 10;

    private float spectrumSearchScoreThreshold = 0.75F;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getSpectrumSearchMzTolerance() {
        return spectrumSearchMzTolerance;
    }

    public void setSpectrumSearchMzTolerance(float spectrumSearchMzTolerance) {
        this.spectrumSearchMzTolerance = spectrumSearchMzTolerance;
    }

    public int getSpectrumSearchNumHits() {
        return spectrumSearchNumHits;
    }

    public void setSpectrumSearchNumHits(int spectrumSearchNumHits) {
        this.spectrumSearchNumHits = spectrumSearchNumHits;
    }

    public float getSpectrumSearchScoreThreshold() {
        return spectrumSearchScoreThreshold;
    }

    public void setSpectrumSearchScoreThreshold(float spectrumSearchScoreThreshold) {
        this.spectrumSearchScoreThreshold = spectrumSearchScoreThreshold;
    }
}
