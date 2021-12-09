package org.dulab.adapcompounddb.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Synonym {

    private long id;
    private Spectrum spectrum;
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
