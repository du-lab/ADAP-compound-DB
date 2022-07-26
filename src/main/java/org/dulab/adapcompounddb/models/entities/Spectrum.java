package org.dulab.adapcompounddb.models.entities;

import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.IdentifierType;
import org.dulab.adapcompounddb.site.services.utils.IsotopicDistributionUtils;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

//    @OneToMany(targetEntity = Identifier.class, mappedBy = "spectrum", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
//    private List<Identifier> identifiers;

    @ElementCollection
    @CollectionTable(name = "Identifier",
            joinColumns = {@JoinColumn(name = "SpectrumId", referencedColumnName = "Id")})
    @MapKeyColumn(name = "type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "value")
    private Map<IdentifierType, String> identifiers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FileId", referencedColumnName = "Id")
    private File file;

    @NotNull(message = "Spectrum: peak list is required.")
    @Valid
    @OneToMany(targetEntity = Peak.class, mappedBy = "spectrum", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<Peak> peaks;

    @OneToMany(targetEntity = Isotope.class, mappedBy = "spectrum", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<Isotope> isotopes;

    @OneToMany(targetEntity = SpectrumProperty.class, mappedBy = "spectrum", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<SpectrumProperty> properties;

    @OneToMany(targetEntity = Synonym.class, mappedBy = "spectrum", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<Synonym> synonyms;

    @OneToMany(targetEntity = SpectrumMatch.class, mappedBy = "querySpectrum", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SpectrumMatch> matches;

    @OneToMany(mappedBy = "matchSpectrum", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SpectrumMatch> matches2;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "ClusterId", referencedColumnName = "Id")
    private SpectrumCluster cluster;

    private boolean consensus;
    private boolean reference;
    private boolean inHouseReference;
    private boolean clusterable;
    private boolean integerMz;

    private Double precursor;
    private String precursorType;
    private Double retentionTime;
    private Double retentionIndex;
    private Double significance;
    private Double mass;
    private String formula;

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

    private String canonicalSmiles; //CC Edits
    private String inChi; //CC Edits
    private String inChiKey; //CC Edits

    private double omegaFactor;

    @NotNull(message = "Spectrum: the field Chromatography Type is requirexd.")
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

    public Map<IdentifierType, String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Map<IdentifierType, String> identifiers) {
        this.identifiers = identifiers;
    }

    public String getStringOfIdentifiers() {
        if (identifiers == null)
            return null;
        return identifiers.entrySet().stream()
                .map(e -> String.format("%s (%s)", e.getValue(), e.getKey()))
                .collect(Collectors.joining(", "));
    }

    public void addIdentifier(IdentifierType identifierType, String value) {
        if (value == null || value.trim().isEmpty()) return;
        if (identifiers == null)
            identifiers = new HashMap<>();
        identifiers.put(identifierType, value);
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

    public String getShortName() {
        if (name != null && name.startsWith("[Ref Spec] "))
            return name.substring(11);
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Synonym> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<Synonym> synonyms) {
        this.synonyms = synonyms;
    }

    public String getStringOfSynonyms() {
        if (synonyms == null) return null;
        return synonyms.stream()
                .map(Synonym::getName)
                .collect(Collectors.joining(", "));
    }

    public File getFile() {
        return file;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getCanonicalSmiles() {
        return canonicalSmiles;
    } //CC Edits

    public void setCanonicalSmiles(String canonicalSmiles) {
        this.canonicalSmiles = canonicalSmiles;
    } //CC Edits

    public String getInChi() {
        return inChi;
    } //CC Edits

    public void setInChi(String inChi) {
        this.inChi = inChi;
    } //CC Edits

    public String getInChiKey() {
        return inChiKey;
    } //CC Edits

    public void setInChiKey(String inChiKey) {
        this.inChiKey = inChiKey;
    } //CC Edits

    public List<Peak> getPeaks() {
        return peaks;
    }

    public void setPeaks(final List<Peak> peaks) {
        setPeaks(peaks, false);
    }

    /**
     * Setter method for the field `peaks`. If `normalize` is true, this method also
     * - normalized peaks' intensities so that their sum is equal to one,
     * - sets attribute `integerMz` to true if all peaks' m/z values are integer
     *
     * @param peaks     list of peaks
     * @param normalize determined whether the normalization is performed
     */
    public void setPeaks(final List<Peak> peaks, final boolean normalize) {

        this.peaks = peaks;

        if (peaks != null && normalize) {
            // order peaks by the intensity in descendant order
            List<Peak> peakList = peaks.stream()
                    .sorted(Comparator.comparingDouble(Peak::getIntensity).reversed())
                    .collect(Collectors.toList());
            // assign m/z values of the top 16 highest peaks
            if (peakList.size() >= 1) {
                this.setTopMz1(peakList.get(0).getMz());
            }
            if (peakList.size() >= 2) {
                this.setTopMz2(peakList.get(1).getMz());
            }
            if (peakList.size() >= 3) {
                this.setTopMz3(peakList.get(2).getMz());
            }
            if (peakList.size() >= 4) {
                this.setTopMz4(peakList.get(3).getMz());
            }
            if (peakList.size() >= 5) {
                this.setTopMz5(peakList.get(4).getMz());
            }
            if (peakList.size() >= 6) {
                this.setTopMz6(peakList.get(5).getMz());
            }
            if (peakList.size() >= 7) {
                this.setTopMz7(peakList.get(6).getMz());
            }
            if (peakList.size() >= 8) {
                this.setTopMz8(peakList.get(7).getMz());
            }
            if (peakList.size() >= 9) {
                this.setTopMz9(peakList.get(8).getMz());
            }
            if (peakList.size() >= 10) {
                this.setTopMz10(peakList.get(9).getMz());
            }
            if (peakList.size() >= 11) {
                this.setTopMz11(peakList.get(10).getMz());
            }
            if (peakList.size() >= 12) {
                this.setTopMz12(peakList.get(11).getMz());
            }
            if (peakList.size() >= 13) {
                this.setTopMz13(peakList.get(12).getMz());
            }
            if (peakList.size() >= 14) {
                this.setTopMz14(peakList.get(13).getMz());
            }
            if (peakList.size() >= 15) {
                this.setTopMz15(peakList.get(14).getMz());
            }
            if (peakList.size() >= 16) {
                this.setTopMz16(peakList.get(15).getMz());
            }

            //System.out.println(peaks.stream().map(p -> Double.toString(p.getIntensity())).collect(Collectors.joining(",")));

            try {
                final Double maxIntensity = peaks.stream()
                        .mapToDouble(Peak::getIntensity)
                        .max().orElseThrow(() -> new IllegalStateException("Cannot find the maximum intensity"));




                integerMz = true;
                for (final Peak peak : peaks) {
                    peak.setIntensity(peak.getIntensity() / maxIntensity);
                    if (peak.getMz() % 1 != 0)
                        integerMz = false;
                }

                double totalIntensity = peaks.stream()
                        .mapToDouble(Peak::getIntensity)
                        .sum();
                omegaFactor = 1 / (totalIntensity - 0.5);
            }
            catch(Exception e) {}
        }
    }

    public List<Isotope> getIsotopes() {
        return isotopes;
    }

    public void setIsotopes(List<Isotope> isotopes) {
        this.isotopes = isotopes;
    }

    public void setIsotopes(double[] intensities) {
        if (intensities == null) return;

        List<Isotope> isotopes = new ArrayList<>(intensities.length);
        for (int i = 0; i < intensities.length; ++i) {
            Isotope isotope = new Isotope();
            isotope.setIndex(i);
            isotope.setIntensity(intensities[i]);
            isotope.setSpectrum(this);
            isotopes.add(isotope);
        }
        setIsotopes(isotopes);
    }

    public double[] getIsotopesAsArray() {
        if (isotopes != null && !isotopes.isEmpty()) {
            return isotopes.stream()
                    .sorted(Comparator.comparingInt(Isotope::getIndex))
                    .mapToDouble(Isotope::getIntensity)
                    .toArray();
        }
        if (formula != null && !formula.isEmpty()) {
            return IsotopicDistributionUtils.calculateDistributionAsArray(formula);
        }
        return null;
    }

    public String getIsotopesAsString() {
        double[] isotopes = getIsotopesAsArray();
        return isotopes != null ? Arrays.stream(isotopes)
                .mapToObj(x -> String.format("%.2f", x))
                .collect(Collectors.joining(" - ")) : null;
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
     * @param mapping    instance of {@link MetaDataMapping} that contains the property names containing meta information
     */
    public void setProperties(List<SpectrumProperty> properties, MetaDataMapping mapping) {
        this.properties = properties;

        if (mapping != null) {
            properties.forEach(p -> mapping.map(p, this));
        }
    }

    public void addProperty(String name, String value, MetaDataMapping mapping) {

        if (properties == null)
            properties = new ArrayList<>();

        SpectrumProperty property = new SpectrumProperty();
        property.setName(name);
        property.setValue(value);
        property.setSpectrum(this);

        properties.add(property);

        this.setProperties(properties, mapping);
    }

    public void addProperty(String name, String value) {
        addProperty(name, value, null);
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

    public boolean isInHouseReference() {
        return inHouseReference;
    }

    public void setInHouseReference(boolean inHouseReference) {
        this.inHouseReference = inHouseReference;
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

    public String getPrecursorType() {
        return precursorType;
    }

    public void setPrecursorType(String precursorType) {
        this.precursorType = precursorType;
    }

    public Double getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(final Double retentionTime) {
        this.retentionTime = retentionTime;
    }

    public Double getRetentionIndex() {
        return retentionIndex;
    }

    public void setRetentionIndex(Double retentionIndex) {
        this.retentionIndex = retentionIndex;
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

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
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

    public double getOmegaFactor() {
        return omegaFactor;
    }

    public void setOmegaFactor(double omegaFactor) {
        this.omegaFactor = omegaFactor;
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
        if (s1.name == null || s2.name == null || !s1.name.equals(s2.name))
            throw new IllegalStateException("Cannot merge two spectra with different external IDs");

        Spectrum mergedSpectrum = new Spectrum();
        mergedSpectrum.setName(mergeStrings(s1.name, s2.name));
        mergedSpectrum.setExternalId(s1.externalId);
        mergedSpectrum.setChromatographyType(s1.chromatographyType);
        mergedSpectrum.setFile(s1.file);
        mergedSpectrum.setPrecursor(mergeDoublesAsAverage(s1.precursor, s2.precursor, 0.01));
        mergedSpectrum.setPrecursorType(s1.precursorType != null ? s1.precursorType : s2.precursorType);
        mergedSpectrum.setRetentionTime(mergeDoublesAsAverage(s1.retentionTime, s2.retentionTime, 0.1));
        mergedSpectrum.setRetentionIndex(mergeDoublesAsAverage(s1.retentionIndex, s2.retentionIndex, 20.0));
        mergedSpectrum.setSignificance(mergeDoublesAsMaximum(s1.significance, s2.significance, 0.1));
        mergedSpectrum.setMass(mergeDoublesAsAverage(s1.mass, s2.mass, 0.01));
        mergedSpectrum.setPeaks(mergeLists(s1.peaks, s2.peaks), true);
        mergedSpectrum.setIsotopes(mergeLists(s1.isotopes, s2.isotopes));
        mergedSpectrum.setProperties(mergeLists(s1.properties, s2.properties));

        return mergedSpectrum;
    }

    private static String mergeStrings(String s1, String s2) {
        if (s1 == null) return s2;
        if (s2 == null) return s1;
        if (s1.equals(s2)) return s1;
        return String.format("%s | %s", s1, s2);
    }

    private static Double mergeDoublesAsAverage(Double d1, Double d2, double tolerance) {
        if (d1 == null) return d2;
        if (d2 == null) return d1;
        if (Math.abs(d1 - d2) > tolerance) return d1;
        return (d1 + d2) / 2;
    }

    private static Double mergeDoublesAsMaximum(Double d1, Double d2, double tolerance) {
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
}
