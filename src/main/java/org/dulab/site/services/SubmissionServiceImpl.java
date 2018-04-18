package org.dulab.site.services;

import org.dulab.models.Submission;
import org.dulab.site.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;

    @Autowired
    public SubmissionServiceImpl(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Override
    @Transactional
    public Optional<Submission> findSubmission(long submissionId) {
        return Optional.ofNullable(submissionRepository.findOne(submissionId));
    }

    @Override
    @Transactional
    public List<Submission> getSubmissionsByUserId(long userId) {
        return ServiceUtils.toList(submissionRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public void saveSubmission(Submission submission) {
        submissionRepository.save(submission);
    }

    @Override
    @Transactional
    public void deleteSubmission(Submission submission) {
        submissionRepository.delete(submission);

    }

//    private <E> List<E> toList(Iterable<E> iterable) {
//        List<E> list = new ArrayList<>();
//        iterable.forEach(list::add);
//        return list;
//    }
}
