package org.dulab.adapcompounddb.models.entities;

import org.dulab.adapcompounddb.models.FileType;
import org.dulab.adapcompounddb.validation.NotBlank;

import javax.persistence.*;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Submission implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SESSION_ATTRIBUTE_KEY = "submission";

    // *************************
    // ***** Entity Fields *****
    // *************************

    private long id;

    @NotBlank(message = "The field Name is required.")
    private String name;

    private String description;

    @NotNull(message = "Date/Time of submission is required.")
    private Date dateTime;

    @Valid
    private List<SubmissionCategory> categories;

//    @Valid
//    private SubmissionSource source;
//
//    @Valid
//    private SubmissionSpecimen specimen;
//
//    @Valid
//    private SubmissionDisease disease;

    @NotNull(message = "Submission: File list is required.")
    @Valid
    private List<File> files;

    @NotNull(message = "You must log in to submit mass spectra to the library.")
    @Valid
    private UserPrincipal user;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String desription) {
        this.description = desription;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Submission2SubmissionCategory",
            joinColumns = {@JoinColumn(name = "SubmissionId")},
            inverseJoinColumns = {@JoinColumn(name = "SubmissionCategoryId")})
    public List<SubmissionCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<SubmissionCategory> categories) {
        this.categories = categories;
    }

    //    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "SourceId", referencedColumnName = "Id")
//    public SubmissionSource getSource() {
//        return source;
//    }
//
//    public void setSource(SubmissionSource source) {
//        this.source = source;
//    }
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "SpecimenId", referencedColumnName = "Id")
//    public SubmissionSpecimen getSpecimen() {
//        return specimen;
//    }
//
//    public void setSpecimen(SubmissionSpecimen specimen) {
//        this.specimen = specimen;
//    }
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "DiseaseId", referencedColumnName = "Id")
//    public SubmissionDisease getDisease() {
//        return disease;
//    }
//
//    public void setDisease(SubmissionDisease disease) {
//        this.disease = disease;
//    }

    @OneToMany(
            mappedBy = "submission",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "UserPrincipalId", referencedColumnName = "Id")
    public UserPrincipal getUser() {
        return user;
    }

    public void setUser(UserPrincipal user) {
        this.user = user;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

//    public String getFilename() {
//        return filename;
//    }
//
//    public void setFilename(String filename) {
//        this.filename = filename;
//    }
//
//    @Enumerated(EnumType.STRING)
//    public FileType getFileType() {
//        return fileType;
//    }
//
//    public void setFileType(FileType fileType) {
//        this.fileType = fileType;
//    }
//
//    public byte[] getFile() {
//        return file;
//    }
//
//    public void setFile(byte[] file) {
//        this.file = file;
//    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Submission)) return false;
        return id == ((Submission) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public static Submission from(HttpSession session) {
        return session == null ? null : (Submission) session.getAttribute(SESSION_ATTRIBUTE_KEY);
    }

    public static void assign(HttpSession session, Submission submission) {
        session.setAttribute(SESSION_ATTRIBUTE_KEY, submission);
    }

    public static void clear(HttpSession session) {
        session.removeAttribute(SESSION_ATTRIBUTE_KEY);
    }
}
