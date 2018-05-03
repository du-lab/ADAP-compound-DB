package org.dulab.site.services;

import org.dulab.models.entities.Submission;
import org.dulab.models.entities.SubmissionCategory;
import org.dulab.site.repositories.SubmissionCategoryRespository;
import org.dulab.site.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EmptyStackException;
import java.util.List;

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
    public Submission findSubmission(long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(EmptyStackException::new);
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
    public long getSubmissionCountByCategory(long submissionCategoryId) {
        return submissionRepository.countByCategoryId(submissionCategoryId);
    }

    @Override
    @Transactional
    public void saveSubmissionCategory(SubmissionCategory submissionCategory) {
        submissionCategoryRespository.save(submissionCategory);
    }

    @Override
    @Transactional
    public SubmissionCategory getSubmissionCategory(long submissionCategoryId) {
        return submissionCategoryRespository.findById(submissionCategoryId)
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot find Submission Category with ID = " + submissionCategoryId));
    }

    @Override
    @Transactional
    public List<SubmissionCategory> getAllSubmissionCategories() {
        return ServiceUtils.toList(submissionCategoryRespository.findAll());
    }
}
