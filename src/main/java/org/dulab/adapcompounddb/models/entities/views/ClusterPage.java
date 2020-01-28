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
@Table(name="ClusterPage")
@Immutable
@Deprecated
public class ClusterPage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id", updatable=false, nullable=false)
    private Long id;

    @OneToOne
    @JoinColumn(name="id")
    private SpectrumCluster spectrumCluster;

    @Column
    private Double source;

    @Column
    private Double specimen;

    @Column
    private Double treatment;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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
