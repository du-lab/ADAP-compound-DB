package org.dulab.adapcompounddb.models.dto;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;

import java.io.Serializable;
import java.util.Set;

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

    String chromatographyTypeIconPath;
    String chromatographyTypeLabel;

    SpectrumDTO consensusSpectrum;
    private Set<DiversityIndexDTO> diversityIndices;
    private Double aveDiversity;
    private Double minDiversity;
    private Double maxDiversity;
    private Double minPValue;
    private Double diseasePValue;
    private Double speciesPValue;
    private Double sampleSourcePValue;
    private String consensusSpectrumName;

    public SpectrumClusterDTO() {

    }

    public SpectrumClusterDTO spectrumClusterDTO(SpectrumCluster cluster) {
        this.id = cluster.getId();
        this.consensusSpectrumName = cluster.getConsensusSpectrum().getName();
        this.size = cluster.getSize();
        this.diameter = cluster.getDiameter();
        this.aveSignificance = cluster.getAveSignificance();
        this.minPValue = cluster.getMinPValue();
        this.minSignificance = cluster.getMinSignificance();
        this.maxSignificance = cluster.getMaxSignificance();
        this.minDiversity = cluster.getMinDiversity();
        this.maxDiversity = cluster.getMaxDiversity();
        this.aveDiversity = cluster.getAveDiversity();
        this.chromatographyTypeIconPath = cluster.getConsensusSpectrum().getChromatographyType().getIconPath();
        this.chromatographyTypeLabel = cluster.getConsensusSpectrum().getChromatographyType().getLabel();
        return this;
    }


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

    public String getChromatographyTypeIconPath() {
        return chromatographyTypeIconPath;
    }

    public void setChromatographyTypeIconPath(String chromatographyTypeIconPath) {
        this.chromatographyTypeIconPath = chromatographyTypeIconPath;
    }

    public String getChromatographyTypeLabel() {
        return chromatographyTypeLabel;
    }

    public void setChromatographyTypeLabel(String chromatographyTypeLabel) {
        this.chromatographyTypeLabel = chromatographyTypeLabel;
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

    public Double getAveDiversity() {
        return aveDiversity;
    }

    public void setAveDiversity(final Double aveDiversity) {
        this.aveDiversity = aveDiversity;
    }

    public Double getMinDiversity() {
        return minDiversity;
    }

    public void setMinDiversity(final Double minDiversity) {
        this.minDiversity = minDiversity;
    }

    public Double getMaxDiversity() {
        return maxDiversity;
    }

    public void setMaxDiversity(final Double maxDiversity) {
        this.maxDiversity = maxDiversity;
    }

    public Double getMinPValue() {
        return minPValue;
    }

    public void setMinPValue(final Double minPValue) {
        this.minPValue = minPValue;
    }

    public Double getDiseasePValue() {
        return diseasePValue;
    }

    public void setDiseasePValue(Double diseasePValue) {
        this.diseasePValue = diseasePValue;
    }

    public Double getSpeciesPValue() {
        return speciesPValue;
    }

    public void setSpeciesPValue(Double speciesPValue) {
        this.speciesPValue = speciesPValue;
    }

    public Double getSampleSourcePValue() {
        return sampleSourcePValue;
    }

    public void setSampleSourcePValue(Double sampleSourcePValue) {
        this.sampleSourcePValue = sampleSourcePValue;
    }

    public String getConsensusSpectrumName() {
        return consensusSpectrumName;
    }

    public void setConsensusSpectrumName(String consensusSpectrumName) {
        this.consensusSpectrumName = consensusSpectrumName;
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
