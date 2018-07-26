package org.dulab.adapcompounddb.models.entities;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

@Entity
public class SpectrumProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    @NotNull(message = "SpectrumProperty requires to specify Spectrum.")
    @Valid
    private Spectrum spectrum;

    @NotBlank(message = "Spectrum property name is required.")
    private String name;

    private String value;

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
	@JsonIgnore
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
