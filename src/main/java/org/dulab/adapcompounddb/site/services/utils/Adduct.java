package org.dulab.adapcompounddb.site.services.utils;

public class Adduct {

    private final String name;
    private final int numMolecules;
    private final int charge;
    private final int massDifference;

    public Adduct(String name, int numMolecules, int charge, int massDifference) {
        this.name = name;
        this.numMolecules = numMolecules;
        this.charge = charge;
        this.massDifference = massDifference;
    }

    public String getName() {
        return name;
    }

    public int getNumMolecules() {
        return numMolecules;
    }

    public int getCharge() {
        return charge;
    }

    public int getMassDifference() {
        return massDifference;
    }
}
