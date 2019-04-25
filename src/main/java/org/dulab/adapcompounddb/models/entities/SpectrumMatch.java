package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
//@SqlResultSetMapping(
//        name = "SpectrumMatchMapping",
//        entities = @EntityResult(
//                entityClass = SpectrumMatch.class,
//                fields = {
//                        @FieldResult(name = "id", column = "id"),
//                        @FieldResult(name = "querySpectrumId", column = "querySpectrumId"),
//                        @FieldResult(name = "matchSpectrumId", column = "matchSpectrumId"),
//                        @FieldResult(name = "score", column = "score")
//                }
//        )
//)
public class SpectrumMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    //    @NotNull(message = "Query spectrum is required.")
    private Spectrum querySpectrum;

    @Transient
    private String querySpectrumName;

    @NotNull(message = "Match Spectrum is required.")
    private Spectrum matchSpectrum;

    @Transient
    private String matchSpectrumName;

    private double score;

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
        setQuerySpectrumName(querySpectrum.getName());
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade={})
    @JoinColumn(name = "MatchSpectrumId", referencedColumnName = "Id")
    @JsonIgnore
    public Spectrum getMatchSpectrum() {
        return matchSpectrum;
    }

    public void setMatchSpectrum(final Spectrum matchSpectrum) {
        this.matchSpectrum = matchSpectrum;
        setMatchSpectrumName(matchSpectrum.getName());
    }

    public double getScore() {
        return score;
    }

    public void setScore(final double score) {
        this.score = score;
    }

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
        return String.format("Match between spectra ID = %d and ID = %d: %f",
                querySpectrum.getId(), matchSpectrum.getId(), score);
    }

    public String getQuerySpectrumName() {
        return querySpectrumName;
    }

    public void setQuerySpectrumName(final String querySpectrumName) {
        this.querySpectrumName = querySpectrumName;
    }

    public String getMatchSpectrumName() {
        return matchSpectrumName;
    }

    public void setMatchSpectrumName(final String matchSpectrumName) {
        this.matchSpectrumName = matchSpectrumName;
    }
}
