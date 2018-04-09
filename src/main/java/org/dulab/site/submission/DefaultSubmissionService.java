package org.dulab.site.submission;

import org.dulab.site.models.Submission;
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
        if (submission.getId() < 1)
            submissionRepository.add(submission);
        else
            submissionRepository.update(submission);
    }
}
