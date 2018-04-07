package org.dulab.site.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Entity
public class Spectrum implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name = null;

    private long id;

    @NotNull(message = "Spectrum requires to specify Submission.")
    private Submission submission;

    @NotNull(message = "Peak list is required.")
    private List<Peak> peaks;

    @NotNull(message = "Property list is required.")
    private List<SpectrumProperty> properties;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "SubmissionId", referencedColumnName = "Id")
    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    @OneToMany(
            targetEntity = Peak.class,
            mappedBy = "spectrum",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    public List<Peak> getPeaks() {
        return peaks;
    }

    public void setPeaks(List<Peak> peaks) {
        double maxIntensity = peaks.stream()
                .mapToDouble(Peak::getIntensity)
                .max()
                .orElse(Double.NaN);

        if (Double.isNaN(maxIntensity)) return;

        for (Peak peak : peaks)
            peak.setIntensity(100 * peak.getIntensity() / maxIntensity);

        this.peaks = Collections.unmodifiableList(peaks);

    }

    @OneToMany(
            targetEntity = SpectrumProperty.class,
            mappedBy = "spectrum",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    public List<SpectrumProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SpectrumProperty> properties) {
        this.properties = Collections.unmodifiableList(properties);
        for (SpectrumProperty property : properties)
            if (property.getName().equalsIgnoreCase("name"))
                name = property.getValue();
    }

    @Override
    public String toString() {
        return name != null ? name : "UNKNOWN";
    }
}
