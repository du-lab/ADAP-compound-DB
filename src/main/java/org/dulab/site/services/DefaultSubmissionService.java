package org.dulab.site.services;

import org.dulab.models.Submission;
import org.dulab.site.repositories.DefaultSubmissionRepository;
import org.dulab.site.repositories.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultSubmissionService implements SubmissionService {

    private SubmissionRepository submissionRepository;

    public DefaultSubmissionService() {
        submissionRepository = new DefaultSubmissionRepository();
    }

    @Override
    @Transactional
    public Submission findSubmission(long submissionId) {
        return submissionRepository.get(submissionId);
    }

    @Override
    @Transactional
    public void saveSubmission(Submission submission) {
        if (submission.getId() < 1) {
            submissionRepository.add(submission);

            if (submission.getId() > 0 && !submission.getUser().getSubmissions().contains(submission))
                submission.getUser().getSubmissions().add(submission);
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
