package org.dulab.adapcompounddb.models.entities;

import org.dulab.adapcompounddb.models.enums.SearchTaskStatus;

import javax.persistence.*;
import java.util.List;

@Entity
public class SearchTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "submissionId")
    private Submission submission;

    @ElementCollection
    @Column(name="library_id")
    private List<Long> libraryIds;

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserPrincipal user;


    @Enumerated(EnumType.STRING)
    private SearchTaskStatus status = SearchTaskStatus.NOT_STARTED;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public List<Long> getLibraryIds() {
        return libraryIds;
    }

    public void setLibraryIds(List<Long> libraryIds) {
        this.libraryIds = libraryIds;
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

