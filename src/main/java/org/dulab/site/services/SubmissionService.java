package org.dulab.site.services;

import org.dulab.models.Submission;
import org.dulab.models.SubmissionCategory;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Validated
public interface SubmissionService {

    Optional<Submission> findSubmission(long submissionId);

    List<Submission> getSubmissionsByUserId(long userId);

    void saveSubmission(
            @NotNull(message = "The submission is required.")
            @Valid Submission submission);

    void deleteSubmission(Submission submission);

    Optional<SubmissionCategory> getSubmissionCategory(long submissionCategoryId);

    List<SubmissionCategory> getAllSubmissionCategories();
}
