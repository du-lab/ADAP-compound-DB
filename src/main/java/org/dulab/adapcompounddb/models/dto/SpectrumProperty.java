package org.dulab.adapcompounddb.models.dto;

import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.io.Serializable;

public class SpectrumProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private Spectrum spectrum;

    private String name;

    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SpectrumProperty)) return false;
        return id == ((SpectrumProperty) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}
