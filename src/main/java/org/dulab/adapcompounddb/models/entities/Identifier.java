package org.dulab.adapcompounddb.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dulab.adapcompounddb.models.enums.IdentifierType;

import javax.persistence.*;

@Entity
public class Identifier {

    private long id;
    private Spectrum spectrum;
    private IdentifierType type;
    private String value;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "SpectrumId", referencedColumnName = "Id")
    @JsonIgnore
    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    @Enumerated(EnumType.STRING)
    public IdentifierType getType() {
        return type;
    }

    public void setType(IdentifierType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
