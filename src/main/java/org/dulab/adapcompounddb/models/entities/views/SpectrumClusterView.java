package org.dulab.adapcompounddb.models.entities.views;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Deprecated
public class SpectrumClusterView {

    private long uniqueId;
    private long id;
    private Long clusterId;
    private String name;
    private int size;
    private Double score;
    private Double massError;
    private Double massErrorPPM;
    private Double retTimeError;
    private Double averageSignificance;
    private Double minimumSignificance;
    private Double maximumSignificance;
    private Double minPValue;
    private Double diseasePValue;
    private Double SpeciesPValue;
    private Double SampleSourcePValue;
    private ChromatographyType chromatographyType;


    @Id
    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getMassError() {
        return massError;
    }

    public void setMassError(Double error) {
        this.massError = error;
    }

    public Double getMassErrorPPM() {
        return massErrorPPM;
    }

    public void setMassErrorPPM(Double massErrorPPM) {
        this.massErrorPPM = massErrorPPM;
    }

    public Double getRetTimeError() {
        return retTimeError;
    }

    public void setRetTimeError(Double retTimeError) {
        this.retTimeError = retTimeError;
    }

    public Double getAverageSignificance() {
        return averageSignificance;
    }

    public void setAverageSignificance(Double averageSignificance) {
        this.averageSignificance = averageSignificance;
    }

    public Double getMinimumSignificance() {
        return minimumSignificance;
    }

    public void setMinimumSignificance(Double minimumSignificance) {
        this.minimumSignificance = minimumSignificance;
    }

    public Double getMaximumSignificance() {
        return maximumSignificance;
    }

    public void setMaximumSignificance(Double maximumSignificance) {
        this.maximumSignificance = maximumSignificance;
    }

    public Double getMinPValue()
    {
        return minPValue;
    }

    public void setMinPValue(Double minPValue)
    {
        this.minPValue = minPValue;
    }

    public Double getDiseasePValue()
    {
        return diseasePValue;
    }

    public void setDiseasePValue(Double diseasePValue)
    {
        this.diseasePValue = diseasePValue;
    }

    public Double getSpeciesPValue()
    {
        return SpeciesPValue;
    }

    public void setSpeciesPValue(Double speciesPValue)
    {
        SpeciesPValue = speciesPValue;
    }

    public Double getSampleSourcePValue()
    {
        return SampleSourcePValue;
    }

    public void setSampleSourcePValue(Double sampleSourcePValue)
    {
        SampleSourcePValue = sampleSourcePValue;
    }

    @Enumerated(EnumType.STRING)
    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uniqueId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpectrumClusterView)) return false;
        return this.uniqueId == ((SpectrumClusterView) obj).uniqueId;
    }
}


