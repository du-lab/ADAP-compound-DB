package org.dulab.adapcompounddb.models.entities;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.dulab.adapcompounddb.models.enums.SearchTaskStatus;

import javax.persistence.*;
import java.util.List;

@Entity
public class SearchTask {

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
    @CollectionTable(name = "searchTask_library",
        joinColumns = @JoinColumn(name = "searchTaskId"))
    @MapKeyColumn(name="libraryId")
    @Column(name="libraryName")
    private Map<BigInteger, String> libraries;

    @Enumerated(EnumType.STRING)
    private SearchTaskStatus status = SearchTaskStatus.NOT_STARTED;

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
}

