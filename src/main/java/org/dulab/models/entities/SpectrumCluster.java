package org.dulab.models.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class SpectrumCluster implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name = "Cluster";

    // *************************
    // ***** Entity fields *****
    // *************************

    private long id;

    private Spectrum consensusSpectrum;

    private List<Spectrum> spectra;

    // *******************************
    // ***** Getters and setters *****
    // *******************************

    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ConsensusSpectrumId", referencedColumnName = "Id")
    public Spectrum getConsensusSpectrum() {
        return consensusSpectrum;
    }

    public void setConsensusSpectrum(Spectrum consensusSpectrum) {
        this.consensusSpectrum = consensusSpectrum;
    }

    @OneToMany(
            targetEntity = Spectrum.class,
            mappedBy = "cluster",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    public List<Spectrum> getSpectra() {
        return spectra;
    }

    public void setSpectra(List<Spectrum> spectra) {
        this.spectra = spectra;
        this.name = String.format("Cluster of %d spectra", spectra.size());
    }

    // ****************************
    // ***** Standard methods *****
    // ****************************

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SpectrumCluster)) return false;
        return id == ((SpectrumCluster) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
