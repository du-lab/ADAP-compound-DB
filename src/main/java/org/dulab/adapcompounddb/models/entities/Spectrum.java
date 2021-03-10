package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;

@Entity
@SqlResultSetMapping(name = "SpectrumScoreMapping", columns = { @ColumnResult(name = "SpectrumId", type = Long.class),
        @ColumnResult(name = "Score", type = Double.class) })
public class Spectrum implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String NAME_PROPERTY_NAME = "Name";
    private static final String PRECURSOR_MASS_PROPERTY_NAME = "PrecursorMZ";
    private static final String RETENTION_TIME_PROPERTY_NAME = "RT";
    private static final String SIGNIFICANCE_PROPERTY_NAME = "ANOVA_P_VALUE";

    // *************************
    // ***** Entity fields *****
    // *************************

    private String name = null;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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

    private Double topMz1;

    private Double topMz2;

    private Double topMz3;

    private Double topMz4;

    private Double topMz5;

    private Double topMz6;

    private Double topMz7;

    private Double topMz8;

    private Double topMz9;

    private Double topMz10;

    private Double topMz11;

    private Double topMz12;

    private Double topMz13;

    private Double topMz14;

    private Double topMz15;

    private Double topMz16;

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

    public void setPeaks(final List<Peak> peaks, final boolean normalize) {

        this.peaks = peaks;

        if (peaks != null && normalize) {
            // order peaks by the intensity in descendant order
            List<Peak> peakList = peaks.stream()
                    .sorted(Comparator.comparingDouble(Peak::getIntensity).reversed())
                    .collect(Collectors.toList());
            // assign m/z values of the top 16 highest peaks
            if (peakList.size() >= 1){
                this.setTopMz1(peakList.get(0).getMz());
            }
            if (peakList.size() >= 2){
                this.setTopMz2(peakList.get(1).getMz());
            }
            if (peakList.size() >= 3){
                this.setTopMz3(peakList.get(2).getMz());
            }
            if (peakList.size() >= 4){
                this.setTopMz4(peakList.get(3).getMz());
            }
            if (peakList.size() >= 5){
                this.setTopMz5(peakList.get(4).getMz());
            }
            if (peakList.size() >= 6){
                this.setTopMz6(peakList.get(5).getMz());
            }
            if (peakList.size() >= 7){
                this.setTopMz7(peakList.get(6).getMz());
            }
            if (peakList.size() >= 8){
                this.setTopMz8(peakList.get(7).getMz());
            }
            if (peakList.size() >= 9){
                this.setTopMz9(peakList.get(8).getMz());
            }
            if (peakList.size() >= 10){
                this.setTopMz10(peakList.get(9).getMz());
            }
            if (peakList.size() >= 11){
                this.setTopMz11(peakList.get(10).getMz());
            }
            if (peakList.size() >= 12){
                this.setTopMz12(peakList.get(11).getMz());
            }
            if (peakList.size() >= 13){
                //TODO: there should be `setTopMz13`, `setTopMz14`,... here and after
                this.setTopMz13(peakList.get(12).getMz());
            }
            if (peakList.size() >= 14){
                this.setTopMz14(peakList.get(13).getMz());
            }
            if (peakList.size() >= 15){
                this.setTopMz15(peakList.get(14).getMz());
            }
            if (peakList.size() >= 16){
                this.setTopMz16(peakList.get(15).getMz());
            }

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

    public void setProperties(final List<SpectrumProperty> properties) {
        this.properties = properties;
    }

    public void addProperty(final String name, final String value) {

        if (properties == null) {
            properties = new ArrayList<>();
        }

        if (name.equalsIgnoreCase(NAME_PROPERTY_NAME)) {
            setName(value);
        } else if (name.equalsIgnoreCase(PRECURSOR_MASS_PROPERTY_NAME)) {
            setPrecursor(Double.valueOf(value));
        } else if (name.equalsIgnoreCase(SIGNIFICANCE_PROPERTY_NAME)) {
            setSignificance(Double.valueOf(value));
        } else if (name.equalsIgnoreCase(RETENTION_TIME_PROPERTY_NAME)) {
            setRetentionTime(Double.valueOf(value));
        }

        final SpectrumProperty property = new SpectrumProperty();
        property.setName(name);
        property.setValue(value);
        property.setSpectrum(this);
        properties.add(property);
    }

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

    public Double getTopMz1() {
        return topMz1;
    }

    public void setTopMz1(Double topMz1) {
        this.topMz1 = topMz1;
    }

    public Double getTopMz2() {
        return topMz2;
    }

    public void setTopMz2(Double topMz2) {
        this.topMz2 = topMz2;
    }

    public Double getTopMz3() {
        return topMz3;
    }

    public void setTopMz3(Double topMz3) {
        this.topMz3 = topMz3;
    }

    public Double getTopMz4() {
        return topMz4;
    }

    public void setTopMz4(Double topMz4) {
        this.topMz4 = topMz4;
    }

    public Double getTopMz5() {
        return topMz5;
    }

    public void setTopMz5(Double topMz5) {
        this.topMz5 = topMz5;
    }

    public Double getTopMz6() {
        return topMz6;
    }

    public void setTopMz6(Double topMz6) {
        this.topMz6 = topMz6;
    }

    public Double getTopMz7() {
        return topMz7;
    }

    public void setTopMz7(Double topMz7) {
        this.topMz7 = topMz7;
    }

    public Double getTopMz8() {
        return topMz8;
    }

    public void setTopMz8(Double topMz8) {
        this.topMz8 = topMz8;
    }

    public Double getTopMz9() {
        return topMz9;
    }

    public void setTopMz9(Double topMz9) {
        this.topMz9 = topMz9;
    }

    public Double getTopMz10() {
        return topMz10;
    }

    public void setTopMz10(Double topMz10) {
        this.topMz10 = topMz10;
    }

    public Double getTopMz11() {
        return topMz11;
    }

    public void setTopMz11(Double topMz11) {
        this.topMz11 = topMz11;
    }

    public Double getTopMz12() {
        return topMz12;
    }

    public void setTopMz12(Double topMz12) {
        this.topMz12 = topMz12;
    }

    public Double getTopMz13() {
        return topMz13;
    }

    public void setTopMz13(Double topMz13) {
        this.topMz13 = topMz13;
    }

    public Double getTopMz14() {
        return topMz14;
    }

    public void setTopMz14(Double topMz14) {
        this.topMz14 = topMz14;
    }

    public Double getTopMz15() {
        return topMz15;
    }

    public void setTopMz15(Double topMz15) {
        this.topMz15 = topMz15;
    }

    public Double getTopMz16() {
        return topMz16;
    }

    public void setTopMz16(Double topMz16) {
        this.topMz16 = topMz16;
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
        if(id == 0) {
            return super.equals(other);
        }
        return id == ((Spectrum) other).id;
    }

    @Override
    public int hashCode() {
        if(id == 0) {
            return super.hashCode();
        } else {
            return Long.hashCode(id);
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
