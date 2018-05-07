package org.dulab.models.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@SqlResultSetMapping(
        name = "SpectrumScoreMapping",
        columns = {
                @ColumnResult(name = "SpectrumId", type = Long.class),
                @ColumnResult(name = "Score", type = Double.class)
        }
)
public class Spectrum implements Serializable {

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity fields *****
    // *************************

    private String name = null;

    private long id;

//    @NotNull(message = "Spectrum requires to specify Submission.")
    private Submission submission;

    @NotNull(message = "Peak list is required.")
    private List<Peak> peaks;

//    @NotNull(message = "Property list is required.")
    private List<SpectrumProperty> properties;

//    @NotNull(message = "List of spectrum matches is required.")
    private List<SpectrumMatch> matches;

    private SpectrumCluster cluster;

    private boolean consensus;

    // *******************************
    // ***** Getters and setters *****
    // *******************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne(fetch = FetchType.LAZY)
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

        if (peaks == null) return;

        double totalIntensity = peaks.stream()
                .mapToDouble(Peak::getIntensity)
                .sum();

        if (totalIntensity <= 0.0) return;

        for (Peak peak : peaks)
            peak.setIntensity(peak.getIntensity() / totalIntensity);

        this.peaks = peaks;
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

        if (properties == null) return;

        this.properties = properties;
        for (SpectrumProperty property : properties)
            if (property.getName().equalsIgnoreCase("name"))
                name = property.getValue();
    }

    @OneToMany(
            targetEntity = SpectrumMatch.class,
            mappedBy = "querySpectrum",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    public List<SpectrumMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<SpectrumMatch> matches) {
        this.matches = matches;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClusterId", referencedColumnName = "Id")
    public SpectrumCluster getCluster() {
        return cluster;
    }

    public void setCluster(SpectrumCluster cluster) {
        this.cluster = cluster;
    }

    public boolean isConsensus() {
        return consensus;
    }

    public void setConsensus(boolean consensus) {
        this.consensus = consensus;
    }

    // ****************************
    // ***** Standard methods *****
    // ****************************

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Spectrum)) return false;
        return id == ((Spectrum) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return name != null ? name : "UNKNOWN";
    }
}
