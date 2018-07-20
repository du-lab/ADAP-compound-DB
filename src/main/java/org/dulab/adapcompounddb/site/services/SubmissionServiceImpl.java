package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.dto.SubmissionDTO;
import org.dulab.adapcompounddb.models.dto.UserPrincipalDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionCategoryRepository submissionCategoryRepository;
	@Autowired
	protected ObjectMapper jacksonObjectMapper;

    @Autowired
    public SubmissionServiceImpl(SubmissionRepository submissionRepository,
                                 SubmissionCategoryRepository submissionCategoryRepository) {

        this.submissionRepository = submissionRepository;
        this.submissionCategoryRepository = submissionCategoryRepository;
    }

    @Override
    @Transactional
    public SubmissionDTO findSubmission(long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(EmptyStackException::new);
        ObjectMapper objectMapper = new ObjectMapper();
        SubmissionDTO submissionDTO = objectMapper.convertValue(submission, SubmissionDTO.class);
		return submissionDTO;
    }

    @Override
    @Transactional
    public List<Submission> getSubmissionsByUserId(long userId) {
        return ServiceUtils.toList(submissionRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public void saveSubmission(SubmissionDTO submissionDTO) {
    	Submission submission = jacksonObjectMapper.convertValue(submissionDTO, Submission.class);
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


//    // ****************************************
//    // ***** SubmissionSpecimen functions *****
//    // ****************************************
//
//    @Override
//    public List<SubmissionSpecimen> getAllSpecies() {
//        return ServiceUtils.toList(submissionSpecimenRepository.findAll());
//    }
//
//    @Override
//    public long countBySpecimenId(long submissionSpecimenId) {
//        return submissionRepository.countBySpecimenId(submissionSpecimenId);
//    }
//
//    @Override
//    public void saveSubmissionSpecimen(SubmissionSpecimen specimen) {
//        submissionSpecimenRepository.save(specimen);
//    }
//
//    @Override
//    public Optional<SubmissionSpecimen> findSubmissionSpecimen(long submissionSpecimenId) {
//        return submissionSpecimenRepository.findById(submissionSpecimenId);
//    }
//
//    @Override
//    public void deleteSubmissionSpecimen(long submissionSpecimenId) {
//        submissionSpecimenRepository.deleteById(submissionSpecimenId);
//    }
//
//    // **************************************
//    // ***** SubmissionSource functions *****
//    // **************************************
//
//    @Override
//    public List<SubmissionSource> getAllSources() {
//        return ServiceUtils.toList(submissionSourceRepository.findAll());
//    }
//
//    @Override
//    public long countBySourceId(long submissionSourceId) {
//        return submissionRepository.countBySourceId(submissionSourceId);
//    }
//
//    @Override
//    public void saveSubmissionSource(SubmissionSource source) {
//        submissionSourceRepository.save(source);
//    }
//
//    @Override
//    public Optional<SubmissionSource> findSubmissionSource(long submissionSourceId) {
//        return submissionSourceRepository.findById(submissionSourceId);
//    }
//
//    @Override
//    public void deleteSubmissionSource(long submissionSourceId) {
//        submissionSourceRepository.deleteById(submissionSourceId);
//    }
//
//    // ***************************************
//    // ***** SubmissionDisease functions *****
//    // **************************************(
//
//    @Override
//    public List<SubmissionDisease> getAllDiseases() {
//        return ServiceUtils.toList(submissionDiseaseRepository.findAll());
//    }
//
//    @Override
//    public long countByDiseaseId(long submissionDiseaseId) {
//        return submissionRepository.countByDiseaseId(submissionDiseaseId);
//    }
//
//    @Override
//    public void saveSubmissionDisease(SubmissionDisease disease) {
//        submissionDiseaseRepository.save(disease);
//    }
//
//    @Override
//    public Optional<SubmissionDisease> findSubmissionDisease(long submissionDiseaseId) {
//        return submissionDiseaseRepository.findById(submissionDiseaseId);
//    }
//
//    @Override
//    public void deleteSubmissionDisease(long submissionDiseaseId) {
//        submissionDiseaseRepository.deleteById(submissionDiseaseId);
//    }

    @Override
    public List<SubmissionCategory> findAllCategories() {

//        SortedMap<SubmissionCategoryType, List<SubmissionCategory>> categoryMap = new TreeMap<>();
//        for (SubmissionCategoryType categoryType : SubmissionCategoryType.values())
//            categoryMap.put(categoryType, new ArrayList<>());
//
//        submissionCategoryRepository.findAll()
//                .forEach(
//                        category -> categoryMap
//                                .get(category.getCategoryType())
//                                .add(category));

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
