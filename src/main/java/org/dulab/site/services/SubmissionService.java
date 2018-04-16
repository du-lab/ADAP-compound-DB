package org.dulab.site.services;

import org.dulab.models.Submission;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface SubmissionService {

    Submission findSubmission(long submissionId);

    List<Submission> getSubmissionsByUserId(long userId);

    void saveSubmission(
            @NotNull(message = "The submission is required.")
            @Valid Submission submission);

    void deleteSubmission(Submission submission);
}
