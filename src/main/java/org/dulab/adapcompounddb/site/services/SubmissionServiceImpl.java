package org.dulab.adapcompounddb.site.services;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SubmissionDTO;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionCategoryRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionTagRepository;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private static final Logger LOG = LogManager.getLogger();

    //    private static final String DESC = "DESC";
    private final SubmissionRepository submissionRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final SubmissionCategoryRepository submissionCategoryRepository;
    private final SpectrumRepository spectrumRepository;


    @Autowired
    public SubmissionServiceImpl(final SubmissionRepository submissionRepository,
                                 final SubmissionTagRepository submissionTagRepository,
                                 final SubmissionCategoryRepository submissionCategoryRepository,
                                 final SpectrumRepository spectrumRepository) {

        this.submissionRepository = submissionRepository;
        this.submissionTagRepository = submissionTagRepository;
        this.submissionCategoryRepository = submissionCategoryRepository;
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    @Transactional
    public Submission findSubmission(final long submissionId) {
        return submissionRepository.findById(submissionId).orElseThrow(EmptyStackException::new);
    }

    @Override
    @Transactional
    public List<Submission> findSubmissionsByUserId(final long userId) {
        return MappingUtils.toList(submissionRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public DataTableResponse findAllSubmissions(String search, Pageable pageable) {

        Page<Submission> submissionPage = submissionRepository.findAllSubmissions(search, pageable);
        List<Submission> submissions = submissionPage.getContent();

        List<SubmissionDTO> submissionDTOs = new ArrayList<>(0);
        if (!submissions.isEmpty()) {
            long[] submissionIds = submissions.stream().mapToLong(Submission::getId).toArray();
            Map<Long, Boolean> references = MappingUtils.toMap(
                    spectrumRepository.getAllSpectrumReferenceBySubmissionIds(submissionIds));
            Map<Long, Boolean> clusterables = MappingUtils.toMap(
                    spectrumRepository.getAllSpectrumClusterableBySubmissionIds(submissionIds));

            submissionDTOs = submissions.stream()
                    .map(s -> new SubmissionDTO(s, references.get(s.getId()), clusterables.get(s.getId())))
                    .collect(Collectors.toList());
        }

        final DataTableResponse response = new DataTableResponse(submissionDTOs);
        response.setRecordsTotal(submissionPage.getTotalElements());
        response.setRecordsFiltered(submissionPage.getTotalElements());

        return response;
    }

    @Override
    @Transactional
    public List<Submission> findSubmissionsWithTagsByUserId(final long userId) {
        return MappingUtils.toList(submissionRepository.findByUserId(userId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSubmission(final Submission submission) {
        final List<File> fileList = submission.getFiles();

        final Submission submissionObj = submissionRepository.save(submission);

        final List<Long> savedFileIds = new ArrayList<>();
        submissionObj.getFiles().stream().forEach(f -> savedFileIds.add(f.getId()));


        if (fileList.get(0).getSpectra().get(0).getId() == 0) {
            spectrumRepository.saveSpectrumAndPeaks(fileList, savedFileIds);
        }
    }

    @Override
    @Transactional
    public void deleteSubmission(final Submission submission) {
        submissionRepository.delete(submission);
    }

    @Override
    @Transactional
    public void delete(final long submissionId) {
        Optional<Submission> submission = submissionRepository.findById(submissionId);
        if (submission.isPresent())
            submissionRepository.delete(submission.get());
        else
            LOG.warn(String.format(
                    "Fail to delete submission %d because this submission is not in the database", submissionId));
    }

    public List<String> findUniqueTagStrings() {
        List<String> uniqueTags = MappingUtils.toList(submissionTagRepository.findUniqueTagStrings());
        uniqueTags.sort(String.CASE_INSENSITIVE_ORDER);
        return uniqueTags;
    }

    @Override
    public List<SubmissionCategory> findAllCategories() {
        return MappingUtils.toList(submissionCategoryRepository.findAll());
    }

    @Override
    public List<SubmissionCategory> findAllCategories(final SubmissionCategoryType categoryType) {
        return MappingUtils.toList(submissionCategoryRepository.findAllByCategoryType(categoryType));
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

//    @Override
//    public List<String> findTagsFromACluster(final Long clusterId) {
//        final List<Object[]> tagArr = submissionTagRepository.findTagsFromACluster(clusterId);
//        final List<String> tagList = new ArrayList<>();
//        tagArr.forEach(arr -> {
//            tagList.add((String) arr[1]);
//        });
//        return tagList;
//    }

//    public Map<String, List<String>> generateTagMapOfACluster(final Long clusterId) {
//        final List<String> tagList = findTagsFromACluster(clusterId)
//                ;
//        final Map<String, List<String>> tagMap = new HashMap<>(); // source:<src1, src2, src1, src2>
//
//        tagList.forEach(tag -> {
//            final String[] arr = tag.split(":", 2);
//            if(arr.length == 2) {
//                final String key = arr[0].trim();
//                final String value = arr[1].trim();
//
//                List<String> valueList = tagMap.get(key);
//                if(CollectionUtils.isEmpty(valueList)) {
//                    valueList = new ArrayList<>();
//                    tagMap.put(key, valueList);
//                }
//                valueList.add(value);
//            }
//        });
//
//        return tagMap;
//    }

    @Override
    public Map<String, List<String>> groupTags(final List<String> tags) {
        final Map<String, List<String>> tagMap = new HashMap<>();

        tags.forEach(tag -> {
            final String[] arr = tag.split(":", 2);
            if (arr.length == 2) {
                final String key = arr[0].trim();
                final String value = arr[1].trim();

                List<String> valueList = tagMap.get(key);
                if (CollectionUtils.isEmpty(valueList)) {
                    valueList = new ArrayList<>();
                    tagMap.put(key, valueList);
                }
                valueList.add(value);
            }
        });

        return tagMap;
    }
}
