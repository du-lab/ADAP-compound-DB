package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.dto.SubmissionDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.repositories.*;
import org.dulab.adapcompounddb.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final SubmissionCategoryRepository submissionCategoryRepository;

    @Autowired
    public SubmissionServiceImpl(SubmissionRepository submissionRepository,
                                 SubmissionTagRepository submissionTagRepository,
                                 SubmissionCategoryRepository submissionCategoryRepository) {

        this.submissionRepository = submissionRepository;
        this.submissionTagRepository = submissionTagRepository;
        this.submissionCategoryRepository = submissionCategoryRepository;
    }

    @Override
    @Transactional
    public Submission findSubmission(long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(EmptyStackException::new);
    }

    @Override
    @Transactional
    public SubmissionDTO findSubmissionById(long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(EmptyStackException::new);
        ObjectMapperUtils objectMapper = new ObjectMapperUtils();

        SubmissionDTO submissionDTO = objectMapper.map(submission, SubmissionDTO.class);
        if (submission.getTags() != null) {
        	submissionDTO.setTags(submission
                    .getTags()
                    .stream()
                    .map(SubmissionTag::getId)
                    .map(SubmissionTagId::getName)
                    .collect(Collectors.joining(",")));
        }
        if (submission.getCategories() != null) {
        	submissionDTO.setSubmissionCategoryIds(submission
                    .getCategories()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(SubmissionCategory::getId)
                    .collect(Collectors.toList()));
        }
		return submissionDTO;
    }

    @Override
    @Transactional
    public List<Submission> findSubmissionsByUserId(long userId) {
        return ServiceUtils.toList(submissionRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public List<Submission> findSubmissionsWithTagsByUserId(long userId) {
        return ServiceUtils.toList(submissionRepository.findWithTagsByUserId(userId));
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
    public void delete(long submissionId) {
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
    public List<SubmissionCategory> findAllCategories(SubmissionCategoryType categoryType) {
        return ServiceUtils.toList(submissionCategoryRepository.findAllByCategoryType(categoryType));
    }

    @Override
    public long countSubmissionsByCategoryId(long submissionCategoryId) {
//        return submissionRepository.countByCategoryId(submissionCategoryId);
        return submissionCategoryRepository.countSubmissionsBySubmissionCategoryId(submissionCategoryId);
    }

    @Override
    public void saveSubmissionCategory(SubmissionCategory category) {
        submissionCategoryRepository.save(category);
    }

    @Override
    public Optional<SubmissionCategory> findSubmissionCategory(long submissionCategoryId) {
        return submissionCategoryRepository.findById(submissionCategoryId);
    }

    @Override
    public void deleteSubmissionCategory(long submissionCategoryId) {
        submissionCategoryRepository.deleteById(submissionCategoryId);
    }
}
