package org.dulab.site.services;

import org.dulab.models.Submission;
import org.dulab.site.repositories.SubmissionRepositoryImpl;
import org.dulab.site.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;

    @Autowired
    public SubmissionServiceImpl(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Override
    @Transactional
    public Submission findSubmission(long submissionId) {
        return submissionRepository.get(submissionId);
    }

    @Override
    @Transactional
    public List<Submission> getSubmissionsByUserId(long userId) {
        return submissionRepository.getSubmissionsByUserId(userId);
    }

    @Override
    @Transactional
    public void saveSubmission(Submission submission) {
        if (submission.getId() < 1) {
            submissionRepository.add(submission);

//            if (submission.getId() > 0 && !submission.getUser().getSubmissions().contains(submission))
//                submission.getUser().getSubmissions().add(submission);
        }
        else
            submissionRepository.update(submission);
    }

    @Override
    @Transactional
    public void deleteSubmission(Submission submission) {
        submissionRepository.delete(submission);

    }
}
