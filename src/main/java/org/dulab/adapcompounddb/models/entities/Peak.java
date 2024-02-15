package org.dulab.adapcompounddb.models.entities;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import org.hibernate.annotations.BatchSize;

@Entity
public class Peak implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    @NotNull(message = "Peak requires to specify Spectrum.")
    @Valid
    @JsonBackReference
    private Spectrum spectrum;

    private double mz;
    private double intensity;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SpectrumId", referencedColumnName = "Id")
    @JsonIgnore
    @BatchSize(size = 100)
    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public double getMz() {
        return mz;
    }

    public void setMz(double mz) {
        this.mz = mz;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Peak)) return false;
        return id == ((Peak) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
