package org.dulab.models;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Peak implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    @NotNull(message = "Peak requires to specify Spectrum.")
    @Valid
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "SpectrumId", referencedColumnName = "Id")
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
