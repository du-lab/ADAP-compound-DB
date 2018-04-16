package org.dulab.site.repositories;

import org.dulab.site.data.GenericRepository;
import org.dulab.models.Submission;

import java.util.List;

public interface SubmissionRepository extends GenericRepository<Long, Submission> {

    List<Submission> getSubmissionsByUserId(long userId);
}
