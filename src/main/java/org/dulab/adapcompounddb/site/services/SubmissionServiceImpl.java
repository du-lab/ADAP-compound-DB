package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final SubmissionCategoryRepository submissionCategoryRepository;

    @Autowired
    public SubmissionServiceImpl(final SubmissionRepository submissionRepository,
            final SubmissionTagRepository submissionTagRepository,
            final SubmissionCategoryRepository submissionCategoryRepository) {

        this.submissionRepository = submissionRepository;
        this.submissionTagRepository = submissionTagRepository;
        this.submissionCategoryRepository = submissionCategoryRepository;
    }

    @Override
    @Transactional
    public Submission findSubmission(final long submissionId) {
        return submissionRepository.findById(submissionId).orElseThrow(EmptyStackException::new);
    }

    @Override
    @Transactional
    public List<Submission> findSubmissionsByUserId(final long userId) {
        return ServiceUtils.toList(submissionRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public List<Submission> findSubmissionsWithTagsByUserId(final long userId) {
        return ServiceUtils.toList(submissionRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public void saveSubmission(final Submission submission) {
        submissionRepository.save(submission);
    }

    @Override
    @Transactional
    public void deleteSubmission(final Submission submission) {
        submissionRepository.delete(submission);
    }

    @Override
    @Transactional
    public void delete(final long submissionId) {
        submissionRepository.deleteById(submissionId);
    }

    @Override
    public List<String> findAllTags() {
        return ServiceUtils.toList(submissionTagRepository.findUniqueSubmissionTagNames());
    }

    @Override
    public List<SubmissionCategory> findAllCategories() {
        return ServiceUtils.toList(submissionCategoryRepository.findAll());
    }

    @Override
    public List<SubmissionCategory> findAllCategories(final SubmissionCategoryType categoryType) {
        return ServiceUtils.toList(submissionCategoryRepository.findAllByCategoryType(categoryType));
    }

    @Override
    public long countSubmissionsByCategoryId(final long submissionCategoryId) {
//        return submissionRepository.countByCategoryId(submissionCategoryId);
        return submissionCategoryRepository.countSubmissionsBySubmissionCategoryId(submissionCategoryId);
    }

    @Override
    public void saveSubmissionCategory(final SubmissionCategory category) {
        submissionCategoryRepository.save(category);
    }

    @Override
    public Optional<SubmissionCategory> findSubmissionCategory(final long submissionCategoryId) {
        return submissionCategoryRepository.findById(submissionCategoryId);
    }

    @Override
    public void deleteSubmissionCategory(final long submissionCategoryId) {
        submissionCategoryRepository.deleteById(submissionCategoryId);
    }
}
