package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;

@Embeddable
@Deprecated
public class DiversityIndexId implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "DiversityIndex: The field Cluster is required.")
    @Valid
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ClusterId", referencedColumnName = "Id")
    private SpectrumCluster cluster;

    /*@NotNull(message = "DiversityIndexL The field Category Type is required.")
    @Enumerated(EnumType.STRING)*/
    @Transient
    private SubmissionCategoryType categoryType;

    public DiversityIndexId() {}

    public DiversityIndexId(final SpectrumCluster cluster, final SubmissionCategoryType categoryType) {
        this.cluster = cluster;
        this.categoryType = categoryType;
    }

    public SpectrumCluster getCluster() {
        return cluster;
    }

    public void setCluster(final SpectrumCluster cluster) {
        this.cluster = cluster;
    }

    public SubmissionCategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(final SubmissionCategoryType categoryType) {
        this.categoryType = categoryType;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DiversityIndexId)) {
            return false;
        }
        final DiversityIndexId that = (DiversityIndexId) other;
        return Objects.equals(this.cluster, that.cluster)
                && Objects.equals(this.categoryType, that.categoryType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cluster, categoryType);
    }
}
