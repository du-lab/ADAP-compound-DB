package org.dulab.adapcompounddb.models.entities;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class Adduct {

    @Id
    long id;
    private String name;
    private int numMolecules;
    private double mass;
    private int charge;
    @Enumerated(value= EnumType.STRING)
    private ChromatographyType chromatography;

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

    public int getNumMolecules() {
        return numMolecules;
    }

    public void setNumMolecules(int numMolecules) {
        this.numMolecules = numMolecules;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double massDifference) {
        this.mass = massDifference;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public ChromatographyType getChromatography() {
        return chromatography;
    }

    public void setChromatography(ChromatographyType chromatography) {
        this.chromatography = chromatography;
    }


    public double calculateNeutralMass(double mz) {
        return (mz * charge - mass) / numMolecules;
    }
}
