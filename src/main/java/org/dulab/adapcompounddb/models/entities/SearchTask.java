package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.dulab.adapcompounddb.models.enums.SearchTaskStatus;

import javax.persistence.*;
import java.util.List;

@Entity
public class SearchTask implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @EmbeddedId
//    SearchTaskId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="submissionId")
    private Submission submission;
    @ManyToOne
    @JoinColumn(name="userId")
    private UserPrincipal user;

    @NotNull(message = "Date/Time of submission is required.")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "SearchTask2Library", joinColumns = @JoinColumn(name = "searchTaskId"))
    @MapKeyColumn(name="libraryId")
    @Column(name="libraryName")
    private Map<BigInteger, String> libraries = new HashMap<>();

    @Enumerated(EnumType.STRING)
    private SearchTaskStatus status = SearchTaskStatus.NOT_STARTED;

    @Lob
    private byte[] simpleExportData;

    @Lob
    private byte[] advancedExportData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


//    public SearchTaskId getId() {
//        return id;
//    }
//
//    public void setId(SearchTaskId id) {
//        this.id = id;
//    }


    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public Map<BigInteger, String> getLibraries() {
        return libraries;
    }

    public void setLibraries(Map<BigInteger, String> libraries) {
        this.libraries.clear();
        this.libraries.putAll(libraries);
    }

    public SearchTaskStatus getStatus() {
        return status;
    }

    public void setStatus(SearchTaskStatus status) {
        this.status = status;
    }

    public UserPrincipal getUser() {
        return user;
    }

    public void setUser(UserPrincipal user) {
        this.user = user;
    }

    public byte[] getSimpleExportData() {
        return simpleExportData;
    }

    public void setSimpleExportData(byte[] simpleExportData) {
        this.simpleExportData = simpleExportData;
    }

    public byte[] getAdvancedExportData() {
        return advancedExportData;
    }

    public void setAdvancedExportData(byte[] advancedExportData) {
        this.advancedExportData = advancedExportData;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SearchTask)) {
            return false;
        }
        return id == ((SearchTask) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}

