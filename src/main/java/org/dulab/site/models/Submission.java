package org.dulab.site.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Submission {

    private List<Spectrum> spectra;
    private LocalDate submissionDate;
    private LocalTime submissionTime;
    private Long userId;
    private String comments;

    public List<Spectrum> getSpectra() {
        return spectra;
    }

    public void setSpectra(List<Spectrum> spectra) {
        this.spectra = spectra;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(LocalTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
