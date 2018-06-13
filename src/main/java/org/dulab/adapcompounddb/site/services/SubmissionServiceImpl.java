package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Optional;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionCategoryRespository submissionCategoryRespository;
    private final SubmissionSourceRepository submissionSourceRepository;
    private final SubmissionSpecimenRepository submissionSpecimenRepository;
    private final SubmissionDiseaseRepository submissionDiseaseRepository;

    @Autowired
    public SubmissionServiceImpl(SubmissionRepository submissionRepository,
                                 SubmissionCategoryRespository submissionCategoryRespository,
                                 SubmissionSourceRepository submissionSourceRepository,
                                 SubmissionSpecimenRepository submissionSpecimenRepository,
                                 SubmissionDiseaseRepository submissionDiseaseRepository) {

        this.submissionRepository = submissionRepository;
        this.submissionCategoryRespository = submissionCategoryRespository;
        this.submissionSourceRepository = submissionSourceRepository;
        this.submissionSpecimenRepository = submissionSpecimenRepository;
        this.submissionDiseaseRepository = submissionDiseaseRepository;
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
    public void delete(long submissionId) {
        submissionRepository.deleteById(submissionId);
    }

    @Override
    public List<SubmissionDisease> getAllDiseases() {
        return ServiceUtils.toList(submissionDiseaseRepository.findAll());
    }

    // ****************************************
    // ***** SubmissionSpecimen functions *****
    // ****************************************

    @Override
    public List<SubmissionSpecimen> getAllSpecies() {
        return ServiceUtils.toList(submissionSpecimenRepository.findAll());
    }

    @Override
    public long countBySpecimenId(long submissionSpecimenId) {
        return submissionRepository.countBySpecimenId(submissionSpecimenId);
    }

    @Override
    public void saveSubmissionSpecimen(SubmissionSpecimen specimen) {
        submissionSpecimenRepository.save(specimen);
    }

    @Override
    public Optional<SubmissionSpecimen> findSubmissionSpecimen(long submissionSpecimenId) {
        return submissionSpecimenRepository.findById(submissionSpecimenId);
    }

    @Override
    public void deleteSubmissionSpecimen(long submissionSpecimenId) {
        submissionSpecimenRepository.deleteById(submissionSpecimenId);
    }

    // **************************************
    // ***** SubmissionSource functions *****
    // **************************************

    @Override
    public List<SubmissionSource> getAllSources() {
        return ServiceUtils.toList(submissionSourceRepository.findAll());
    }

    @Override
    public long countBySourceId(long submissionSourceId) {
        return submissionRepository.countBySourceId(submissionSourceId);
    }

    @Override
    public void saveSubmissionSource(SubmissionSource source) {
        submissionSourceRepository.save(source);
    }

    @Override
    public Optional<SubmissionSource> findSubmissionSource(long submissionSourceId) {
        return submissionSourceRepository.findById(submissionSourceId);
    }

    @Override
    public void deleteSubmissionSource(long submissionSourceId) {
        submissionSourceRepository.deleteById(submissionSourceId);
    }
}
