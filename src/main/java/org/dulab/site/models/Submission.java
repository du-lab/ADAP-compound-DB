package org.dulab.site.models;

import org.dulab.site.validation.NotBlank;

import javax.persistence.*;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
public class Submission implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SESSION_ATTRIBUTE_KEY = "submission";

    private long id;

    @NotBlank(message = "The field Name is required.")
    private String name;

    @NotBlank(message = "The field Description is required.")
    private String description;

    @NotNull(message = "Date/Time of submission is required.")
    private LocalDateTime dateTime;

    @NotBlank(message = "Filename of the raw file is required.")
    private String filename;

    @NotNull(message = "Type of the raw file is required.")
    private FileType fileType;

    @NotNull(message = "Raw file is required.")
    private byte[] file;

    @NotNull(message = "Chromatography type is required.")
    private ChromatographyType chromatographyType;

    @NotNull (message = "Spectrum list is required.")
    @Valid
    private List<Spectrum> spectra;

    @NotNull (message = "You must log in to submit mass spectra to the library.")
    @Valid
    private UserPrincipal user;

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

    @Enumerated(EnumType.STRING)
    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }

    @OneToMany(
            targetEntity = Spectrum.class,
            mappedBy = "submission",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    public List<Spectrum> getSpectra() {
        return spectra;
    }

    public void setSpectra(List<Spectrum> spectra) {
        this.spectra = spectra;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "UserPrincipalId", referencedColumnName = "Id")
    public UserPrincipal getUser() {
        return user;
    }

    public void setUser(UserPrincipal user) {
        this.user = user;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public static Submission get(HttpSession session) {
        return session == null ? null : (Submission) session.getAttribute(SESSION_ATTRIBUTE_KEY);
    }

    public static void set(HttpSession session, Submission submission) {
        session.setAttribute(SESSION_ATTRIBUTE_KEY, submission);
    }
}
