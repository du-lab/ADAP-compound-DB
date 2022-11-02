package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.dulab.adapcompounddb.models.enums.FileType;

@Entity
public class File implements Comparable<File>, Serializable {

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity Fields *****
    // *************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "File: the field Name is required.")
    private String name;

    @NotNull(message = "File: the field FileType is required.")
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @NotNull(message = "File: the field Submission is required.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SubmissionId", referencedColumnName = "Id")
    private Submission submission;

    @NotNull(message = "File: Spectrum list is required.")
    @Valid
    @OneToMany(
            mappedBy = "file",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REFRESH,
            orphanRemoval = true
    )
    private List<Spectrum> spectra;


    @OneToOne(mappedBy = "file",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    FileContent fileContent;

    private int size;


    // *******************************
    // ***** Getters and Setters *****
    // *******************************


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


    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(final FileType fileType) {
        this.fileType = fileType;
    }




    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(final Submission submission) {
        this.submission = submission;
    }


    public List<Spectrum> getSpectra() {
        return spectra;
    }

    public void setSpectra(final List<Spectrum> spectra) {
        this.spectra = spectra;
        this.size = (spectra != null) ? spectra.size() : 0;
    }

    public FileContent getFileContent() {
        return fileContent;
    }

    public void setFileContent(FileContent fileContent) {
        this.fileContent = fileContent;
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    // *************************
    // ***** Other methods *****
    // *************************


    @Override
    public int compareTo(@org.jetbrains.annotations.NotNull File o) {
        return -Integer.compare(this.fileType.getPriority(), o.fileType.getPriority());
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof File)) {
            return false;
        }
        if(id == null || id == 0) {
            return super.equals(other);
        }
        return id.equals(((File) other).id);
    }

    @Override
    public int hashCode() {
        return (id != null) ? Long.hashCode(id) : super.hashCode();
    }
}
