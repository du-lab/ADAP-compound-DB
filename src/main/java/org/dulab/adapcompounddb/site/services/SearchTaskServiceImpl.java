package org.dulab.adapcompounddb.site.services;

import java.util.List;
import java.util.Optional;
import org.dulab.adapcompounddb.models.entities.SearchTask;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.repositories.SearchTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchTaskServiceImpl implements SearchTaskService{

  @Autowired
  SearchTaskRepository searchTaskRepository;
  @Transactional
  @Override
  public List<SearchTask> findSearchTaskByUser(UserPrincipal user) {
    return searchTaskRepository.findByUserId(user.getId());
  }

  @Transactional
  @Override
  public SearchTask findByUserIdAndSubmissionId(long userId, long submissionId) {
    return searchTaskRepository.findByUserIdAndSubmissionId(userId, submissionId);
  }

  @Transactional
  @Override
  public SearchTask save(SearchTask searchTask) {
    return searchTaskRepository.save(searchTask);
  }

  @Transactional
  @Override
  public SearchTask findByUserIdAndSubmission(long id, Submission submission) {
    return searchTaskRepository.findByUserIdAndSubmission(id, submission);
  }
}
