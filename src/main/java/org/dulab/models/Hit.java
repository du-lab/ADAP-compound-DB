package org.dulab.models;

import org.dulab.models.entities.Spectrum;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

public class Hit implements Serializable {

    private static final long serialVersionUID = 1L;

//    @NotNull(message = "Spectrum is required")
    private Spectrum spectrum;

    private double score;

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
}
