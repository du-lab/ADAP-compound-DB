package org.dulab.adapcompounddb.models.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
public class SpectrumCluster implements Serializable {

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity fields *****
    // *************************

    private long id;

    private Spectrum consensusSpectrum;

    @NotNull(message = "Diameter of cluster is required.")
    private Double diameter;

    @NotNull(message = "Size of cluster is required.")
    private Integer size;

    private Double aveSignificance;
    private Double minSignificance;
    private Double maxSignificance;

    private List<Spectrum> spectra;

    private Set<DiversityIndex> diversityIndices;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ConsensusSpectrumId", referencedColumnName = "Id")
    @JsonIgnore
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

    @OneToMany(
            mappedBy = "cluster",
            fetch = FetchType.LAZY,
            cascade = {})
    public List<Spectrum> getSpectra() {
        return spectra;
    }

    public void setSpectra(List<Spectrum> spectra) {
        this.spectra = spectra;
    }

    @OneToMany(
            mappedBy = "id.cluster",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    public Set<DiversityIndex> getDiversityIndices() {
        return diversityIndices;
    }

    public void setDiversityIndices(Set<DiversityIndex> diversityIndices) {
        this.diversityIndices = diversityIndices;
    }

    public Double getAveSignificance() {
        return aveSignificance;
    }

    public void setAveSignificance(Double aveSignificance) {
        this.aveSignificance = aveSignificance;
    }

    public Double getMinSignificance() {
        return minSignificance;
    }

    public void setMinSignificance(Double minSignificance) {
        this.minSignificance = minSignificance;
    }

    public Double getMaxSignificance() {
        return maxSignificance;
    }

    public void setMaxSignificance(Double maxSignificance) {
        this.maxSignificance = maxSignificance;
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
        return "Cluster ID = " + getId();
    }
}
