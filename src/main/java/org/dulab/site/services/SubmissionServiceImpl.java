package org.dulab.site.services;

import org.dulab.models.Submission;
import org.dulab.models.SubmissionCategory;
import org.dulab.site.repositories.SubmissionCategoryRespository;
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
    private final SubmissionCategoryRespository submissionCategoryRespository;

    @Autowired
    public SubmissionServiceImpl(SubmissionRepository submissionRepository,
                                 SubmissionCategoryRespository submissionCategoryRespository) {
        this.submissionRepository = submissionRepository;
        this.submissionCategoryRespository = submissionCategoryRespository;
    }

    @Override
    @Transactional
    public Optional<Submission> findSubmission(long submissionId) {
        return submissionRepository.findById(submissionId);
//        return Optional.ofNullable(submissionRepository.findOne(submissionId));
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

    @Override
    @Transactional
    public Optional<SubmissionCategory> getSubmissionCategory(long submissionCategoryId) {
        return submissionCategoryRespository.findById(submissionCategoryId);
//        return submissionCategoryRespository.findOne(submissionCategoryId);
    }

    @Override
    @Transactional
    public List<SubmissionCategory> getAllSubmissionCategories() {
        return ServiceUtils.toList(submissionCategoryRespository.findAll());
    }
}
