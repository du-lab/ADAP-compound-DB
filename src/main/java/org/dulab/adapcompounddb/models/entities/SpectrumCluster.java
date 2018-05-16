package org.dulab.adapcompounddb.models.entities;

import org.dulab.adapcompounddb.models.ChromatographyType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "Diameter of cluster is required.")
    private Double diameter;

    @NotNull(message = "Size of cluster is required.")
    private Integer size;

    @NotNull(message = "Chromatography type is required.")
    private ChromatographyType chromatographyType;

    private List<Spectrum> spectra;

    // *******************************
    // ***** Getters and setters *****
    // *******************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ConsensusSpectrumId", referencedColumnName = "Id")
    public Spectrum getConsensusSpectrum() {
        return consensusSpectrum;
    }

    public void setConsensusSpectrum(Spectrum consensusSpectrum) {
        this.consensusSpectrum = consensusSpectrum;
    }

    public Double getDiameter() {
        return diameter;
    }

    public void setDiameter(Double diameter) {
        this.diameter = diameter;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Enumerated(value = EnumType.STRING)
    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }

    @OneToMany(
            mappedBy = "cluster",
            fetch = FetchType.LAZY,
            cascade = CascadeType.DETACH)
    public List<Spectrum> getSpectra() {
        return spectra;
    }

    public void setSpectra(List<Spectrum> spectra) {
        this.spectra = spectra;
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
