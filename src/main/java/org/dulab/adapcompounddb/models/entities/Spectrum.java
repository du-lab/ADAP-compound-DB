package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;

@Entity
@SqlResultSetMapping(name = "SpectrumScoreMapping", columns = {@ColumnResult(name = "SpectrumId", type = Long.class),
        @ColumnResult(name = "Score", type = Double.class)})
public class Spectrum implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]+.?[0-9]+)");

    // *************************
    // ***** Entity fields *****
    // *************************

    private String name = null;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String externalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FileId", referencedColumnName = "Id")
    private File file;

    @NotNull(message = "Spectrum: peak list is required.")
    @Valid
    @OneToMany(targetEntity = Peak.class, mappedBy = "spectrum", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<Peak> peaks;

    @OneToMany(targetEntity = SpectrumProperty.class, mappedBy = "spectrum", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<SpectrumProperty> properties;

    @OneToMany(targetEntity = SpectrumMatch.class, mappedBy = "querySpectrum", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SpectrumMatch> matches;

    @OneToMany(mappedBy = "matchSpectrum", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SpectrumMatch> matches2;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "ClusterId", referencedColumnName = "Id")
    private SpectrumCluster cluster;

    private boolean consensus;

    private boolean reference;

    private boolean clusterable;

    private boolean integerMz;

    private Double precursor;

    private Double retentionTime;

    private Double significance;

    private Double molecularWeight;

    @NotNull(message = "Spectrum: the field Chromatography Type is required.")
    @Enumerated(EnumType.STRING)
    private ChromatographyType chromatographyType;

    // *******************************
    // ***** Getters and setters *****
    // *******************************

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        String fullName = name;
        if (fullName == null) {
            fullName = "UNKNOWN";
        }
        if (reference) {
            fullName = "[Ref Spec] " + fullName;
        }
        if (consensus) {
            fullName = "[Con Spec] " + fullName;
        }
        return fullName;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public List<Peak> getPeaks() {
        return peaks;
    }

    public void setPeaks(final List<Peak> peaks) {
        setPeaks(peaks, false);
    }

    /** Setter method for the field `peaks`. If `normalize` is true, this method also
     * - normalized peaks' intensities so that their sum is equal to one,
     * - sets attribute `integerMz` to true if all peaks' m/z values are integer
     *
     * @param peaks list of peaks
     * @param normalize determined whether the normalization is performed
     */
    public void setPeaks(final List<Peak> peaks, final boolean normalize) {

        this.peaks = peaks;

        if (peaks != null && normalize) {

            final double totalIntensity = peaks.stream()
                    .mapToDouble(Peak::getIntensity)
                    .sum();

            integerMz = true;
            for (final Peak peak : peaks) {
                peak.setIntensity(peak.getIntensity() / totalIntensity);
                if (peak.getMz() % 1 != 0)
                    integerMz = false;
            }
        }
    }

    public List<SpectrumProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SpectrumProperty> properties) {
        this.setProperties(properties, null);
    }

    /**
     * Setter method for the field `properties`. If `mapping` is given, this methods also tries to fill out other
     * fields like `name`, `precursorMz`, `retentionTime`, etc.
     *
     * @param properties list of properties
     * @param mapping instance of {@link MetaDataMapping} that contains the property names containing meta information
     */
    public void setProperties(List<SpectrumProperty> properties, MetaDataMapping mapping) {
        this.properties = properties;

        if (mapping != null) {
            for (SpectrumProperty property : properties) {
                String propertyName = property.getName();
                String propertyValue = property.getValue();
                if (propertyName.equalsIgnoreCase(mapping.getNameField()))
                    this.setName(propertyValue);
                else if (propertyName.equalsIgnoreCase(mapping.getExternalIdField()))
                    this.setExternalId(propertyValue);
                else if (propertyName.equalsIgnoreCase(mapping.getPrecursorMzField()))
                    this.setPrecursor(parseDouble(propertyValue));
                else if (propertyName.equalsIgnoreCase(mapping.getRetTimeField()))
                    this.setRetentionTime(parseDouble(propertyValue));
                else if (propertyName.equalsIgnoreCase(mapping.getMolecularWeight()))
                    this.setMolecularWeight(parseDouble(propertyValue));
            }
        }
    }

//    public void addProperty(String name, String value) {
//
//        if (properties == null) properties = new ArrayList<>();
//
//        SpectrumProperty property = new SpectrumProperty();
//        property.setName(name);
//        property.setValue(value);
//        property.setSpectrum(this);
//        properties.add(property);
//
////        if (name.equalsIgnoreCase(NAME_PROPERTY_NAME)) {
////            setName(value);
////        } else if (name.equalsIgnoreCase(PRECURSOR_MASS_PROPERTY_NAME)) {
////            setPrecursor(Double.valueOf(value));
////        } else if (name.equalsIgnoreCase(SIGNIFICANCE_PROPERTY_NAME)) {
////            setSignificance(Double.valueOf(value));
////        } else if (name.equalsIgnoreCase(RETENTION_TIME_PROPERTY_NAME)) {
////            setRetentionTime(Double.valueOf(value));
////        }
//
//
//    }

    public List<SpectrumMatch> getMatches() {
        return matches;
    }

    public void setMatches(final List<SpectrumMatch> matches) {
        this.matches = matches;
    }

    public List<SpectrumMatch> getMatches2() {
        return matches2;
    }

    public void setMatches2(final List<SpectrumMatch> matches2) {
        this.matches2 = matches2;
    }

    public SpectrumCluster getCluster() {
        return cluster;
    }

    public void setCluster(final SpectrumCluster cluster) {
        this.cluster = cluster;
    }

    public boolean isConsensus() {
        return consensus;
    }

    public void setConsensus(final boolean consensus) {
        this.consensus = consensus;
    }

    public boolean isReference() {
        return reference;
    }

    public void setReference(final boolean reference) {
        this.reference = reference;
    }

    public boolean isClusterable() {
        return clusterable;
    }

    public void setClusterable(boolean clusterable) {
        this.clusterable = clusterable;
    }

    public boolean isIntegerMz() {
        return integerMz;
    }

    public void setIntegerMz(boolean integerMz) {
        this.integerMz = integerMz;
    }

    public Double getPrecursor() {
        return precursor;
    }

    public void setPrecursor(final Double precursor) {
        this.precursor = precursor;
    }

    public Double getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(final Double retentionTime) {
        this.retentionTime = retentionTime;
    }

    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(final ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }

    public Double getSignificance() {
        return significance;
    }

    public void setSignificance(final Double significance) {
        this.significance = significance;
    }

    public Double getMolecularWeight() {
        return molecularWeight;
    }

    public void setMolecularWeight(Double molecularWeight) {
        this.molecularWeight = molecularWeight;
    }

    // ****************************
    // ***** Standard methods *****
    // ****************************

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Spectrum)) {
            return false;
        }
        if (id == 0) {
            return super.equals(other);
        }
        return id == ((Spectrum) other).id;
    }

    @Override
    public int hashCode() {
        if (id == 0) {
            return super.hashCode();
        } else {
            return Long.hashCode(id);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    // *************************
    // ***** Other methods *****
    // *************************

    public static Spectrum merge(Spectrum s1, Spectrum s2) throws IllegalStateException {
        if (s1.externalId == null || s2.externalId == null || !s1.externalId.equals(s2.externalId))
            throw new IllegalStateException("Cannot merge two spectra with different external IDs");

        Spectrum mergedSpectrum = new Spectrum();
        mergedSpectrum.setName(mergeStrings(s1.name, s2.name));
        mergedSpectrum.setExternalId(s1.externalId);
        mergedSpectrum.setChromatographyType(s1.chromatographyType);
        mergedSpectrum.setFile(s1.file);
        mergedSpectrum.setPrecursor(mergeDoublesByAverage(s1.precursor, s2.precursor, 0.01));
        mergedSpectrum.setRetentionTime(mergeDoublesByAverage(s1.retentionTime, s2.retentionTime, 0.1));
        mergedSpectrum.setSignificance(mergeDoublesByMaximum(s1.significance, s2.significance, 0.1));
        mergedSpectrum.setMolecularWeight(mergeDoublesByAverage(s1.molecularWeight, s2.molecularWeight, 0.01));
        mergedSpectrum.setPeaks(mergeLists(s1.peaks, s2.peaks), true);
        mergedSpectrum.setProperties(mergeLists(s1.properties, s2.properties));

        return mergedSpectrum;
    }

    private static String mergeStrings(String s1, String s2) {
        if (s1 == null) return s2;
        if (s2 == null) return s1;
        if (s1.equals(s2)) return s1;
        return String.format("%s | %s", s1, s2);
    }

    private static Double mergeDoublesByAverage(Double d1, Double d2, double tolerance) {
        if (d1 == null) return d2;
        if (d2 == null) return d1;
        if (Math.abs(d1 - d2) > tolerance) return d1;
        return (d1 + d2) / 2;
    }

    private static Double mergeDoublesByMaximum(Double d1, Double d2, double tolerance) {
        if (d1 == null) return d2;
        if (d2 == null) return d1;
        if (Math.abs(d1 - d2) > tolerance) return d1;
        return Math.max(d1, d2);
    }

    private static <E> List<E> mergeLists(List<E> l1, List<E> l2) {
        if (l1 == null) return l2;
        if (l2 == null) return l1;
        List<E> list = new ArrayList<>(l1.size() + l2.size());
        list.addAll(l1);
        list.addAll(l2);
        return list;
    }

    private static Double parseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NullPointerException | NumberFormatException e) {
            return null;
        }
    }
}
