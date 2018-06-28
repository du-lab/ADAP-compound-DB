package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Submission;
import org.springframework.data.repository.CrudRepository;

public interface SubmissionRepository extends CrudRepository<Submission, Long> {

    Iterable<Submission> findByUserId(long userPrincipalId);

    void deleteById(long id);

    long countBySourceId(long submissionSourceId);

    long countBySpecimenId(long submissionSpecimenId);

    long countByDiseaseId(long submissionDiseaseId);
}
