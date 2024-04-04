package org.dulab.adapcompounddb.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class PeakDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private long id;
    private double mz;
    private double intensity;

    public PeakDTO(long id, double mz, double intensity) {
        this.id = id;
        this.mz = mz;
        this.intensity = intensity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getMz() {
        return mz;
    }

    public void setMz(double mz) {
        this.mz = mz;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PeakDTO)) {
            return false;
        }
        return id == ((PeakDTO) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }


}
