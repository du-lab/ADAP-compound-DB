package org.dulab.adapcompounddb.models.dto;

import org.dulab.adapcompounddb.models.entities.SubmissionTag;

import java.util.List;

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

    private String externalId;

    private List<SubmissionTag> studyTag;

    private String description;

    public SubmissionMatchDTO(long submissionId, String submissionName, int score, String externalId,
                              List<SubmissionTag> studyTag, String description) {
        this.submissionId = submissionId;
        this.submissionName = submissionName;
        this.score = score;
        this.externalId = externalId;
        this.studyTag = studyTag;
        this.description = description;
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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<SubmissionTag> getStudyTag() {
        return studyTag;
    }

    public void setStudyTag(List<SubmissionTag> studyTag) {
        this.studyTag = studyTag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
