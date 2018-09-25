package org.dulab.adapcompounddb.models.entities.views;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name="clusterpage1")
@Immutable
public class ClusterPage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id", updatable=false, nullable=false)
    private Long id;

    /*@Column
    private String name;

    @Column
    private Integer size;

    @Column
    private Double diameter;

    @Column
    private Double aveSignificance;

    @Column
    private Double minSignificance;

    @Column
    private Double maxSignificance;

    @Enumerated(EnumType.STRING)
    ChromatographyType chromatographyType;*/

    @OneToOne
    @JoinColumn(name="id")
    SpectrumCluster spectrumCluster;

    @Column
    Double source;

    @Column
    Double specimen;

    @Column
    Double treatment;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    /* public String getName() {
        return soename;
    }

    public Integer getSize() {
        return size;
    }

    public Double getDiameter() {
        return diameter;
    }

    public Double getAveSignificance() {
        return aveSignificance;
    }

    public Double getMinSignificance() {
        return minSignificance;
    }

    public Double getMaxSignificance() {
        return maxSignificance;
    }

    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }*/

    public SpectrumCluster getSpectrumCluster() {
        return spectrumCluster;
    }

    public void setSpectrumCluster(final SpectrumCluster spectrumCluster) {
        this.spectrumCluster = spectrumCluster;
    }

    public Double getSource() {
        return source;
    }

    public void setSource(final Double source) {
        this.source = source;
    }

    public Double getSpecimen() {
        return specimen;
    }

    public void setSpecimen(final Double specimen) {
        this.specimen = specimen;
    }

    public Double getTreatment() {
        return treatment;
    }

    public void setTreatment(final Double treatment) {
        this.treatment = treatment;
    }

}
