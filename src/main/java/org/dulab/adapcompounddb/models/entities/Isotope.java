package org.dulab.adapcompounddb.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Isotope implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    @NotNull(message = "Peak requires to specify Spectrum.")
    @Valid
    private Spectrum spectrum;

    private int index;
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
    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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
        if (!(other instanceof Isotope)) return false;
        return id == ((Isotope) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
