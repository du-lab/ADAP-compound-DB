package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class SpectrumMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private Spectrum querySpectrum;

    private Spectrum matchSpectrum;

    private Double score;
    private Double isotopicSimilarity;
    private Double precursorError;
    private Double precursorErrorPPM;
    private String precursorType;
    private Double massError;
    private Double massErrorPPM;
    private Double retTimeError;
    private Double retIndexError;

    private Long userPrincipalId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade={})
    @JoinColumn(name = "QuerySpectrumId", referencedColumnName = "Id")
    @JsonIgnore
    public Spectrum getQuerySpectrum() {
        return querySpectrum;
    }

    public void setQuerySpectrum(final Spectrum querySpectrum) {
        this.querySpectrum = querySpectrum;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade={})
    @JoinColumn(name = "MatchSpectrumId", referencedColumnName = "Id")
    @JsonIgnore
    public Spectrum getMatchSpectrum() {
        return matchSpectrum ;
    }

    public void setMatchSpectrum(final Spectrum matchSpectrum) {
        this.matchSpectrum = matchSpectrum;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(final Double score) {
        this.score = score;
    }

    public Double getIsotopicSimilarity() {
        return isotopicSimilarity;
    }

    public void setIsotopicSimilarity(Double isotopicSimilarityScore) {
        this.isotopicSimilarity = isotopicSimilarityScore;
    }

    public Double getPrecursorError() {
        return precursorError;
    }

    public void setPrecursorError(Double precursorError) {
        this.precursorError = precursorError;
    }

    public Double getPrecursorErrorPPM() {
        return precursorErrorPPM;
    }

    public void setPrecursorErrorPPM(Double precursorErrorPPM) {
        this.precursorErrorPPM = precursorErrorPPM;
    }

    @Transient
    public String getPrecursorType() {
        return precursorType;
    }

    public void setPrecursorType(String precursorType) {
        this.precursorType = precursorType;
    }

    public Double getMassError() {
        return massError;
    }

    public void setMassError(Double massError) {
        this.massError = massError;
    }

    public Double getMassErrorPPM() {
        return massErrorPPM;
    }

    public void setMassErrorPPM(Double massErrorPPM) {
        this.massErrorPPM = massErrorPPM;
    }

    public Double getRetTimeError() {
        return retTimeError;
    }

    public void setRetTimeError(Double retTimeError) {
        this.retTimeError = retTimeError;
    }

    public Double getRetIndexError() {
        return retIndexError;
    }

    public void setRetIndexError(Double retIndexError) {
        this.retIndexError = retIndexError;
    }

    public Long getUserPrincipalId() {
        return userPrincipalId;
    }

    public void setUserPrincipalId(Long userPrincipalId) {
        this.userPrincipalId = userPrincipalId;
    }

    public SpectrumMatch(){}

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpectrumMatch)) {
            return false;
        }
        return querySpectrum.equals(((SpectrumMatch) other).querySpectrum)
                && matchSpectrum.equals(((SpectrumMatch) other).matchSpectrum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(querySpectrum.getId(), matchSpectrum.getId());
    }

    @Override
    public String toString() {
         return (querySpectrum != null && matchSpectrum != null && score != null) ?
                 String.format("Match between spectra ID = %d and ID = %d: %f", querySpectrum.getId(), matchSpectrum.getId(), score)
                 :"No match";
    }
}
