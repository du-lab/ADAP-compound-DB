package org.dulab.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Entity
//@SqlResultSetMappings(
//    @SqlResultSetMapping(
//        name = "SpectrumScoreMapping",
//        columns = {
//                @ColumnResult(name = "SpectrumId", type = Long.class),
//                @ColumnResult(name = "Score", type = Double.class)
//        }
//    )
//)
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

        double totalIntensity = peaks.stream()
                .mapToDouble(Peak::getIntensity)
                .sum();

        if (totalIntensity <= 0.0) return;

        for (Peak peak : peaks)
            peak.setIntensity(peak.getIntensity() / totalIntensity);

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
