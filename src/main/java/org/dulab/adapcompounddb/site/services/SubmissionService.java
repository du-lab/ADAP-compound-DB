package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Validated
public interface SubmissionService {

    Submission findSubmission(long submissionId);

    List<Submission> getSubmissionsByUserId(long userId);

    void saveSubmission(
            @NotNull(message = "The submission is required.")
            @Valid Submission submission);

    void deleteSubmission(Submission submission);

    void delete(long submissionId);

    void saveSubmissionCategory(SubmissionCategory submissionCategory);

    long getSubmissionCountByCategory(long submissionCategoryId);

    SubmissionCategory getSubmissionCategory(long submissionCategoryId);

    List<SubmissionCategory> getAllSubmissionCategories();

    List<SubmissionSource> getAllSources();

    List<SubmissionSpecimen> getAllSpecies();

    List<SubmissionDisease> getAllDiseases();

    long countBySpecimenId(long submissionSpecimenId);

    Optional<SubmissionSpecimen> findSubmissionSpecimen(long submissionSpecimenId);

    void saveSubmissionSpecimen(SubmissionSpecimen specimen);

    void deleteSubmissionSpecimen(long submissionSpecimenId);
}
