package org.dulab.site.repositories;

import org.dulab.site.data.GenericJpaRepository;
import org.dulab.models.Submission;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultSubmissionRepository extends GenericJpaRepository <Long, Submission>
        implements SubmissionRepository {

    public DefaultSubmissionRepository() {
        super(Long.class, Submission.class);
    }
}
