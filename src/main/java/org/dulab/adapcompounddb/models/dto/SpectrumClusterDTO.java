package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class SpectrumClusterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity fields *****
    // *************************

    private long id;

    private SpectrumDTO consensusSpectrum;

    @NotNull(message = "Diameter of cluster is required.")
    private Double diameter;

    @NotNull(message = "Size of cluster is required.")
    private Integer size;

    private Double aveSignificance;
    private Double minSignificance;
    private Double maxSignificance;
    private Set<DiversityIndexDTO> diversityIndices;

    // *******************************
    // ***** Getters and setters *****
    // *******************************

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public SpectrumDTO getConsensusSpectrum() {
        return consensusSpectrum;
    }

    public void setConsensusSpectrum(final SpectrumDTO consensusSpectrum) {
        this.consensusSpectrum = consensusSpectrum;
    }

    public Double getDiameter() {
        return diameter;
    }

    public void setDiameter(final Double diameter) {
        this.diameter = diameter;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(final Integer size) {
        this.size = size;
    }

    public Double getAveSignificance() {
        return aveSignificance;
    }

    public void setAveSignificance(final Double aveSignificance) {
        this.aveSignificance = aveSignificance;
    }

    public Double getMinSignificance() {
        return minSignificance;
    }

    public void setMinSignificance(final Double minSignificance) {
        this.minSignificance = minSignificance;
    }

    public Double getMaxSignificance() {
        return maxSignificance;
    }

    public void setMaxSignificance(final Double maxSignificance) {
        this.maxSignificance = maxSignificance;
    }

    public Set<DiversityIndexDTO> getDiversityIndices() {
        return diversityIndices;
    }

    public void setDiversityIndices(final Set<DiversityIndexDTO> diversityIndices) {
        this.diversityIndices = diversityIndices;
    }

    // ****************************
    // ***** Standard methods *****
    // ****************************

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpectrumClusterDTO)) {
            return false;
        }
        return id == ((SpectrumClusterDTO) other).id;
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
