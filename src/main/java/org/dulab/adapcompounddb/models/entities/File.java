package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.dulab.adapcompounddb.models.FileType;

@Entity
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity Fields *****
    // *************************

    private Long id;

    @NotBlank(message = "File: the field Name is required.")
    private String name;

    @NotNull(message = "File: the field FileType is required.")
    private FileType fileType;

    @NotNull(message = "File: the field Content is required.")
    private byte[] content;

    @NotNull(message = "File: the field Submission is required.")
    private Submission submission;

    @NotNull(message = "File: Spectrum list is required.")
    @Valid
    private List<Spectrum> spectra;

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Enumerated(EnumType.STRING)
    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(final FileType fileType) {
        this.fileType = fileType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(final byte[] content) {
        this.content = content;
    }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "SubmissionId", referencedColumnName = "Id")
    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(final Submission submission) {
        this.submission = submission;
    }

    @OneToMany(
            mappedBy = "file",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REFRESH,
            orphanRemoval = true
            )
    public List<Spectrum> getSpectra() {
        return spectra;
    }

    public void setSpectra(final List<Spectrum> spectra) {
        this.spectra = spectra;
    }

    // *************************
    // ***** Other methods *****
    // *************************

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof File)) {
            return false;
        }
        if(id == 0) {
            super.equals(other);
        }
        return id == ((File) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
