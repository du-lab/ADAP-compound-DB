package org.dulab.models.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class SpectrumMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    @NotNull(message = "Query spectrum is required.")
    private Spectrum querySpectrum;

    @NotNull(message = "Match Spectrum is required.")
    private Spectrum matchSpectrum;

    private double score;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "QuerySpectrumId", referencedColumnName = "Id")
    public Spectrum getQuerySpectrum() {
        return querySpectrum;
    }

    public void setQuerySpectrum(Spectrum querySpectrum) {
        this.querySpectrum = querySpectrum;
    }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "MatchSpectrumId", referencedColumnName = "Id")
    public Spectrum getMatchSpectrum() {
        return matchSpectrum;
    }

    public void setMatchSpectrum(Spectrum matchSpectrum) {
        this.matchSpectrum = matchSpectrum;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
