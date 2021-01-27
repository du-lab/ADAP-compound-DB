package org.dulab.adapcompounddb.models.entities.views;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class MassSearchResult {

    private long id;
    private String name;
    private double molecularWeight;
    private double error;
    private ChromatographyType chromatographyType;

    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMolecularWeight() {
        return molecularWeight;
    }

    public void setMolecularWeight(double molecularWeight) {
        this.molecularWeight = molecularWeight;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    @Enumerated(EnumType.STRING)
    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }
}
