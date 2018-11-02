package org.dulab.adapcompounddb.site.services;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
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
import org.dulab.adapcompounddb.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private static final String DESC = "DESC";
    private final SubmissionRepository submissionRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final SubmissionCategoryRepository submissionCategoryRepository;
    private final SpectrumRepository spectrumRepository;

    private static enum ColumnInformation {
        ID(0, "id"), DATE(1, "dateTime"), NAME(2, "name"), USER(3, "user.username");

        private int position;
        private String sortColumnName;

        private ColumnInformation(final int position, final String sortColumnName) {
            this.position = position;
            this.sortColumnName = sortColumnName;
        }

        public int getPosition() {
            return position;
        }

        public String getSortColumnName() {
            return sortColumnName;
        }

        public static String getColumnNameFromPosition(final int position) {
            String columnName = null;
            for (final ColumnInformation columnInformation : ColumnInformation.values()) {
                if (position == columnInformation.getPosition()) {
                    columnName = columnInformation.getSortColumnName();
                }
            }
            return columnName;
        }

        public static String getDefaultSortColumn() {
            return ColumnInformation.DATE.getSortColumnName();
        }
    }

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
        return ServiceUtils.toList(submissionRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public DataTableResponse findAllSubmissionsForResponse(final String searchStr, final Integer start,
            final Integer length, final Integer column, String orderDirection) {
        final ObjectMapperUtils objectMapper = new ObjectMapperUtils();
        Pageable pageable = null;

        String sortColumn = ColumnInformation.getColumnNameFromPosition(column);
        if (sortColumn == null) {
            sortColumn = ColumnInformation.getDefaultSortColumn();
            orderDirection = DESC;
        }
        if (sortColumn != null) {
            final Sort sort = new Sort(Sort.Direction.fromString(orderDirection), sortColumn);
            pageable = PageRequest.of(start / length, length, sort);
        } else {
            pageable = PageRequest.of(start / length, length);
        }

        final Page<Submission> submissionPage = submissionRepository.findAllSubmissions(searchStr, pageable);
        final List<SubmissionDTO> submissionList = objectMapper.map(submissionPage.getContent(), SubmissionDTO.class);

        for (final SubmissionDTO sub : submissionList) {
            final Integer reference = spectrumRepository.getSpectrumReferenceOfSubmissionIfSame(sub.getId());
            sub.setAllSpectrumReference(reference);
        }

        final DataTableResponse response = new DataTableResponse(submissionList);
        response.setRecordsTotal(submissionPage.getTotalElements());
        response.setRecordsFiltered(submissionPage.getTotalElements());

        return response;
    }

    @Override
    @Transactional
    public List<Submission> findSubmissionsWithTagsByUserId(final long userId) {
        return ServiceUtils.toList(submissionRepository.findByUserId(userId));
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void saveSubmission(final Submission submission) {
        final List<File> fileList = submission.getFiles();

        final Submission submissionObj = submissionRepository.save(submission);

        final List<Long> savedFileIds = new ArrayList<>();
        submissionObj.getFiles().stream().forEach(f->savedFileIds.add(f.getId()));


        if(fileList.get(0).getSpectra().get(0).getId() == 0) {
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

    @Override
    public List<String> findTagsFromACluster(final Long clusterId) {
        final List<Object[]> tagArr = submissionTagRepository.findTagsFromACluster(clusterId);
        final List<String> tagList = new ArrayList<>();
        tagArr.forEach(arr -> {
            tagList.add((String) arr[1]);
        });
        return tagList;
    }

    @Override
    public Map<String, List<String>> groupTags(final List<String> tags) {
        final Map<String, List<String>> tagMap = new HashMap<>();

        tags.forEach(tag -> {
            final String[] arr = tag.split(":", 2);
            if(arr.length == 2) {
                final String key = arr[0].trim();
                final String value = arr[1].trim();

                List<String> valueList = tagMap.get(key);
                if(CollectionUtils.isEmpty(valueList)) {
                    valueList = new ArrayList<>();
                    tagMap.put(key, valueList);
                }
                valueList.add(value);
            }
        });

        return tagMap;
    }
}
