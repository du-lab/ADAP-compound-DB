package org.dulab.site.repositories;

import org.dulab.site.data.GenericRepository;
import org.dulab.models.Submission;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SubmissionRepository extends CrudRepository<Submission, Long> {

    Iterable<Submission> findByUserId(long userPrincipalId);

    long countByCategoryId(long submissionCategoryId);
}
