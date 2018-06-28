package org.dulab.adapcompounddb.models.entities;

import org.dulab.adapcompounddb.models.FileType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity Fields *****
    // *************************

    private long id;

    @NotBlank(message = "File: the field Name is required.")
    private String name;

    @NotNull(message = "File: the field FileType is required.")
    private FileType fileType;

    @NotNull(message = "File: the field Content is required.")
    private byte[] content;

    @NotNull(message = "File: the field Submission is required.")
    private Submission submission;

    @NotNull(message = "File: Spectrum list is required.")
    private List<Spectrum> spectra;

    // *******************************
    // ***** Getters and Setters *****
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

    @Enumerated(EnumType.STRING)
    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "SubmissionId", referencedColumnName = "Id")
    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    @OneToMany(
            mappedBy = "file",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    public List<Spectrum> getSpectra() {
        return spectra;
    }

    public void setSpectra(List<Spectrum> spectra) {
        this.spectra = spectra;
    }

    // *************************
    // ***** Other methods *****
    // *************************

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof File)) return false;
        return id == ((File) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
