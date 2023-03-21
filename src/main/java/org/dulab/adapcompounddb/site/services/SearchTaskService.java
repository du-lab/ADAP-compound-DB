package org.dulab.adapcompounddb.site.services;

import java.util.List;
import java.util.Optional;
import org.dulab.adapcompounddb.models.entities.SearchTask;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;

public interface SearchTaskService {
    List<SearchTask> findSearchTaskByUser(UserPrincipal user);

    SearchTask findByUserIdAndSubmissionId(long id, long submissionId);

    SearchTask save(SearchTask searchTask);

    SearchTask findByUserIdAndSubmission(long id, Submission submission);
}
