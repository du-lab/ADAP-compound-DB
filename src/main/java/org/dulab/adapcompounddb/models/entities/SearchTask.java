package org.dulab.adapcompounddb.models.entities;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.dulab.adapcompounddb.models.enums.StatusType;

@Entity
public class SearchTask {

  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;

  @OneToMany
  private List<Submission> submissions;

  @OneToMany
  private List<Submission> libraries;
  @OneToMany
  private List<UserPrincipal> userPrincipals;

  @Enumerated(EnumType.STRING)
  private StatusType status;

  String name;

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public List<Submission> getSubmissions() {
    return submissions;
  }

  public void setSubmissions(List<Submission> submissions) {
    this.submissions = submissions;
  }

  public List<UserPrincipal> getUserPrincipals() {
    return userPrincipals;
  }

  public void setUserPrincipals(List<UserPrincipal> userPrincipals) {
    this.userPrincipals = userPrincipals;
  }

  public List<Submission> getLibraries() {
    return libraries;
  }

  public void setLibraries(List<Submission> libraries) {
    this.libraries = libraries;
  }

  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof SearchTask)) {
      return false;
    }
    if (id == 0) {
      return super.equals(other);
    }
    return id == ((SearchTask) other).id;
  }

  @Override
  public int hashCode() {
    if (id == 0) {
      return super.hashCode();
    } else {
      return Long.hashCode(id);
    }
  }
}
