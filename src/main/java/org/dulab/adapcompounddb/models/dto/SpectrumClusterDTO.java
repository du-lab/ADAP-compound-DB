package org.dulab.adapcompounddb.models.dto;

import java.io.Serializable;
import java.util.Set;

import org.dulab.adapcompounddb.models.ChromatographyType;

public class SpectrumClusterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity fields *****
    // *************************

    private long id;
    private Integer size;

    private Double diameter;
    private Double aveSignificance;
    private Double minSignificance;
    private Double maxSignificance;

    ChromatographyType chromatographyType;

    SpectrumDTO consensusSpectrum;
    private Set<DiversityIndexDTO> diversityIndices;

    Double source;
    Double specimen;
    Double treatment;

    // *******************************
    // ***** Getters and setters *****
    // *******************************

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

    public Integer getSize() {
        return size;
    }

    public void setSize(final Integer size) {
        this.size = size;
    }

    public Double getDiameter() {
        return diameter;
    }

    public void setDiameter(final Double diameter) {
        this.diameter = diameter;
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

    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(final ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }

    public Set<DiversityIndexDTO> getDiversityIndices() {
        return diversityIndices;
    }

    public void setDiversityIndices(final Set<DiversityIndexDTO> diversityIndices) {
        this.diversityIndices = diversityIndices;
    }

    public Double getSource() {
        return source;
    }

    public void setSource(final Double source) {
        this.source = source;
    }

    public Double getSpecimen() {
        return specimen;
    }

    public void setSpecimen(final Double specimen) {
        this.specimen = specimen;
    }

    public Double getTreatment() {
        return treatment;
    }

    public void setTreatment(final Double treatment) {
        this.treatment = treatment;
    }


    // ****************************
    // ***** Standard methods *****
    // ****************************

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "Cluster ID = " + getId();
    }
}
