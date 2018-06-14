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


    // ****************************************
    // ***** SubmissionSpecimen functions *****
    // ****************************************

    List<SubmissionSpecimen> getAllSpecies();

    long countBySpecimenId(long submissionSpecimenId);

    Optional<SubmissionSpecimen> findSubmissionSpecimen(long submissionSpecimenId);

    void saveSubmissionSpecimen(SubmissionSpecimen specimen);

    void deleteSubmissionSpecimen(long submissionSpecimenId);

    // **************************************
    // ***** SubmissionSource functions *****
    // **************************************

    List<SubmissionSource> getAllSources();

    long countBySourceId(long submissionSourceId);

    Optional<SubmissionSource> findSubmissionSource(long submissionSourceId);

    void saveSubmissionSource(SubmissionSource source);

    void deleteSubmissionSource(long submissionSourceId);

    // ***************************************
    // ***** SubmissionDisease functions *****
    // ***************************************

    List<SubmissionDisease> getAllDiseases();

    long countByDiseaseId(long submissionDiseaseId);

    Optional<SubmissionDisease> findSubmissionDisease(long submissionDiseaseId);

    void saveSubmissionDisease(SubmissionDisease disease);

    void deleteSubmissionDisease(long submissionDiseaseId);
}
