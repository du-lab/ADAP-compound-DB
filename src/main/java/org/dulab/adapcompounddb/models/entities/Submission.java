package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.MassSpectrometryType;
import org.hibernate.validator.constraints.URL;

@Entity
public class Submission implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SESSION_ATTRIBUTE_KEY = "submission";

    @Transient
    public static final String SAVE_SUBMISSION = "SAVE_SUBMISSION";
    @Transient
    public static final String DELETE_SUBMISSION = "DELETE_SUBMISSION";

    // *************************
    // ***** Entity Fields *****
    // *************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "The field Name is required.")
    private String name;

    private String description;

    @NotNull(message = "Date/Time of submission is required.")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    @Enumerated(EnumType.STRING)
    private MassSpectrometryType massSpectrometryType;

    @Valid
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Submission2SubmissionCategory",
            joinColumns = {@JoinColumn(name = "SubmissionId")},
            inverseJoinColumns = {@JoinColumn(name = "SubmissionCategoryId")})
    private List<SubmissionCategory> categories;

    @Valid
    @OneToMany(
            mappedBy = "submission",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SubmissionTag> tags;

    @NotNull(message = "Submission: File list is required.")
    @Valid
    @OneToMany(
            mappedBy = "submission",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.ALL},
            orphanRemoval = true
    )
    private List<File> files;

    @OneToMany(mappedBy = "submission", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SearchTask> searchTasks;

    @NotNull(message = "You must log in to submit mass spectra to the library.")
    @Valid
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "UserPrincipalId", referencedColumnName = "Id")
    private UserPrincipal user;

    @URL(message = "Submission: The field Reference must be a valid URL.")
    private String url;
    private String externalId;

    private String source;
    private boolean isPrivate = false;

    private boolean clusterable;
    private boolean raw;
    private int size;
    
    @Transient
    private boolean isSearchable;

    @NotNull(message = "Spectrum: the field Chromatography Type is requirexd.")
    @Enumerated(EnumType.STRING)
    private ChromatographyType chromatographyType;

    private boolean isReference;
    private boolean isInHouseReference;

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "New Study";
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String desription) {
        this.description = desription;
    }

    public MassSpectrometryType getMassSpectrometryType() {
        return massSpectrometryType;
    }

    public void setMassSpectrometryType(MassSpectrometryType massSpectrometryType) {
        this.massSpectrometryType = massSpectrometryType;
    }

    public List<SubmissionCategory> getCategories() {
        return categories;
    }

    public void setCategories(final List<SubmissionCategory> categories) {
        this.categories = categories;
    }

    public SubmissionCategory getCategory(final SubmissionCategoryType type) {
        if (categories != null) {
            return getCategories().stream()
                    .filter(c -> c.getCategoryType() == type)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    //properties
    public List<SubmissionTag> getTags() {
        return tags;
    }

    public void setTags(final List<SubmissionTag> tags) {
        this.tags = tags;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(final List<File> files) {
        this.files = files;
        this.size = (files != null) ? files.stream().mapToInt(File::getSize).sum() : 0;
    }

    public List<SearchTask> getSearchTasks() {
        return searchTasks;
    }

    public void setSearchTasks(List<SearchTask> searchTasks) {
        this.searchTasks = searchTasks;
    }

    public UserPrincipal getUser() {
        return user;
    }

    public void setUser(final UserPrincipal user) {
        this.user = user;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(final Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String reference) {
        this.url = url;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isClusterable() {
        return clusterable;
    }

    public void setClusterable(boolean clusterable) {
        this.clusterable = clusterable;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isRaw() {
        return raw;
    }

    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }

    public boolean isInHouseReference() {
        return isInHouseReference;
    }

    public boolean getIsReference() {
        return isReference;
    }

    public void setIsReference(boolean isReference) {
        this.isReference = isReference;
    }

    public void setInHouseReference(boolean inHouseReference) {
        isInHouseReference = inHouseReference;
    }


    // *************************
    // ***** Other methods *****
    // *************************



    public boolean isAuthorized(final UserPrincipal user) {
        boolean authorized = true;
        if (user == null) {
            authorized = false;
        } else if (user.isAdmin()) {
            authorized = true;
        } else if (id != 0) {
            authorized = StringUtils.equals(user.getUsername(), this.getUser().getUsername());
        }

        return authorized;
    }

    @Transient
    public String getTagValue(String key) {
        SubmissionTag tag = null;
        if (tags != null)
            tag = tags.stream()
                    .filter(t -> t.getTagKey().equalsIgnoreCase(key))
                    .findAny().orElse(null);

        return tag != null ? tag.getTagValue() : null;
    }

    @Transient
    public String getTagsAsString() {
        return tags == null ? "" : getTags()
                .stream()
                .map(SubmissionTag::getTagValue)
                .collect(Collectors.joining(", "))
                .trim();
    }

    /**
     * Determines whether the submission can be searched with the group search.
     * @return true for a non-raw submission or when the chromatography type is either LC_MSMS__POS or LC_MSMS_NEG
     */
    @Transient
    public boolean isSearchable() {
//        if (!isRaw()) return true;
//
//        Set<ChromatographyType> chromatographyTypes = files.stream()
//                .flatMap(f -> f.getSpectra().stream())
//                .map(Spectrum::getChromatographyType)
//                .collect(Collectors.toSet());
//
//        if (chromatographyTypes.size() != 1)
//            return false;
//
//        ChromatographyType chromatographyType = chromatographyTypes.iterator().next();
//        return chromatographyType == ChromatographyType.LC_MSMS_POS
//                || chromatographyType == ChromatographyType.LC_MSMS_NEG;
        return isSearchable;
    }

    public void setSearchable(boolean searchable) {
        isSearchable = searchable;
    }
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Submission)) {
            return false;
        }
        return id == ((Submission) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public static Submission from(final HttpSession session) {
        return session == null ? null : (Submission) session.getAttribute(SESSION_ATTRIBUTE_KEY);
    }

    public static void assign(final HttpSession session, final Submission submission) {
        session.setAttribute(SESSION_ATTRIBUTE_KEY, submission);
    }

    public static void clear(final HttpSession session) {
        session.removeAttribute(SESSION_ATTRIBUTE_KEY);
    }
}
