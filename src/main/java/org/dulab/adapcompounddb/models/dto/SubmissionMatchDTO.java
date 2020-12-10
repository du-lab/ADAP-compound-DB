package org.dulab.adapcompounddb.models.dto;

/**
 * This class is used to return study matches from StudySearchService.studySearch() and display them on study_search.jsp
 */
public class SubmissionMatchDTO {

    // Id of the matched submission
    private long submissionId;

    // name of the matched submission
    private String submissionName;

    // Bray-Curtis dissimilarity score multiplied by 1000
    private int score;

    public SubmissionMatchDTO(long submissionId, String submissionName, int score) {
        this.submissionId = submissionId;
        this.submissionName = submissionName;
        this.score = score;
    }

    public long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(long submissionId) {
        this.submissionId = submissionId;
    }

    public String getSubmissionName() {
        return submissionName;
    }

    public void setSubmissionName(String submissionName) {
        this.submissionName = submissionName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
