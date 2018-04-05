package org.dulab.site.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Spectrum implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name = null;

    private long id;
    private List<Peak> peaks;
    private List<SpectrumProperty> properties;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Peak> getPeaks() {
        return peaks;
    }

    public void setPeaks(List<Peak> peaks) {
        double maxIntensity = peaks.stream()
                .mapToDouble(Peak::getIntensity)
                .max()
                .orElseThrow(() -> new IllegalArgumentException("Peak list is empty"));

        for (Peak peak : peaks)
            peak.setIntensity(100 * peak.getIntensity() / maxIntensity);

        this.peaks = peaks;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<SpectrumProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SpectrumProperty> properties) {
        this.properties = properties;
        for (SpectrumProperty property : properties)
            if (property.getName().equalsIgnoreCase("name"))
                name = property.getValue();
    }

    @Override
    public String toString() {
        return name != null ? name : "UNKNOWN";
    }
}
