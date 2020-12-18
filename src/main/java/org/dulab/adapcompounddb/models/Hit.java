package org.dulab.adapcompounddb.models;

import org.dulab.adapcompounddb.models.entities.Spectrum;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

public class Hit implements Serializable {

    private static final long serialVersionUID = 1L;

    private Spectrum spectrum;

    private double score;

    private ConfidenceLevel confidenceLevel;

    @OneToOne(optional = false)
    @JoinColumn(name = "spectrumId", referencedColumnName = "Id")
    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public ConfidenceLevel getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(ConfidenceLevel confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }
}
