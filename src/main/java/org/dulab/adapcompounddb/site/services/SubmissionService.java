package org.dulab.adapcompounddb.site.services;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.dto.SubmissionDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.repositories.*;
import org.dulab.adapcompounddb.site.services.utils.DataUtils;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private enum ColumnInformation {

        ID(0, "id"),
        DATETIME(1, "datetime"),
        NAME(2, "name"),
        EXTERNAL_ID(3, "externalId"),
        PROPERTIES(4,"properties");

        private final int position;
        private final String sortColumnName;

        public int getPosition() {
            return position;
        }

        public String getSortColumnName() {
            return sortColumnName;
        }

        ColumnInformation(int position, String sortColumnName) {
            this.position = position;
            this.sortColumnName = sortColumnName;
        }

        public static String getColumnNameFromPosition(int position) {
            for (ColumnInformation columnInformation : ColumnInformation.values())
                if (position == columnInformation.getPosition())
                    return columnInformation.getSortColumnName();
            return null;
        }
    }
    private static final Logger LOG = LogManager.getLogger(SubmissionService.class);

    //    private static final String DESC = "DESC";
    private final SubmissionRepository submissionRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final SubmissionCategoryRepository submissionCategoryRepository;
    private final SpectrumRepository spectrumRepository;
    private final MultiFetchRepository multiFetchRepository;


    @Autowired
    public SubmissionService(final SubmissionRepository submissionRepository,
                             final SubmissionTagRepository submissionTagRepository,
                             final SubmissionCategoryRepository submissionCategoryRepository,
                             final SpectrumRepository spectrumRepository,
                             final MultiFetchRepository multiFetchRepository) {

        this.submissionRepository = submissionRepository;
        this.submissionTagRepository = submissionTagRepository;
        this.submissionCategoryRepository = submissionCategoryRepository;
        this.spectrumRepository = spectrumRepository;
        this.multiFetchRepository = multiFetchRepository;
    }

    @Transactional
    public Submission findSubmission(final long submissionId) {
        return submissionRepository.findById(submissionId).orElseThrow(EmptyStackException::new);
    }

    public Submission fetchSubmission(long submissionId) {
        return multiFetchRepository.getSubmissionWithFilesSpectraPeaksIsotopes(submissionId);
    }

    @Transactional
    public List<Submission> findSubmissionsByUserId(final long userId) {
        return MappingUtils.toList(submissionRepository.findByUserId(userId));
    }

    @Transactional
    public DataTableResponse findAllSubmissions(String search, Pageable pageable) {

        Page<Submission> submissionPage = submissionRepository.findAllSubmissions(search, pageable);
        List<Submission> submissions = submissionPage.getContent();

        List<SubmissionDTO> submissionDTOs = new ArrayList<>(0);
        if (!submissions.isEmpty()) {
            long[] submissionIds = submissions.stream().mapToLong(Submission::getId).toArray();
            Map<Long, Boolean> references = MappingUtils.toMap(
                    spectrumRepository.getAllSpectrumReferenceBySubmissionIds(submissionIds));
            Map<Long, Boolean> inHouseReferences = MappingUtils.toMap(
                    spectrumRepository.getAllSpectrumInHouseReferenceBySubmissionIds(submissionIds));
            Map<Long, Boolean> clusterables = MappingUtils.toMap(
                    spectrumRepository.getAllSpectrumClusterableBySubmissionIds(submissionIds));

            submissionDTOs = submissions.stream()
                    .map(s -> new SubmissionDTO(s, references.get(s.getId()), inHouseReferences.get(s.getId()),
                            clusterables.get(s.getId())))
                    .collect(Collectors.toList());
        }

        final DataTableResponse response = new DataTableResponse(submissionDTOs);
        response.setRecordsTotal(submissionPage.getTotalElements());
        response.setRecordsFiltered(submissionPage.getTotalElements());

        return response;
    }

    public Iterable<Submission> findAllPublicLibraries(){
        return submissionRepository.findByPrivateFalseAndReferenceTrue();
    }

//    @Transactional
    public List<Submission> findSubmissionsWithTagsByUserId(long userId) {
        return MappingUtils.toList(submissionRepository.findWithTagsByUserId(userId));
    }

    public List<Submission> findSubmissionsByExternalId(String externalId) {
        return MappingUtils.toList(submissionRepository.findByExternalId(externalId));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSubmission(final Submission submission) {
        final List<File> fileList = submission.getFiles();

        final Submission submissionObj = submissionRepository.save(submission);

        final List<Long> savedFileIds = new ArrayList<>();
        submissionObj.getFiles().forEach(f -> savedFileIds.add(f.getId()));

        Set<Long> ids = fileList.stream()
                .map(File::getSpectra).filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(Spectrum::getId)
                .collect(Collectors.toSet());
//        if (fileList.get(0).getSpectra().get(0).getId() == 0) {
        if (ids.contains(0L)) {
            spectrumRepository.saveSpectra(fileList, savedFileIds);
        }
    }

    @Transactional
    public void deleteSubmission(final Submission submission) {
        submissionRepository.delete(submission);
    }

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

    public List<SubmissionCategory> findAllCategories() {
        return MappingUtils.toList(submissionCategoryRepository.findAll());
    }

    public List<SubmissionCategory> findAllCategories(final SubmissionCategoryType categoryType) {
        return MappingUtils.toList(submissionCategoryRepository.findAllByCategoryType(categoryType));
    }

    public long countSubmissionsByCategoryId(final long submissionCategoryId) {
        //        return submissionRepository.countByCategoryId(submissionCategoryId);
        return submissionCategoryRepository.countSubmissionsBySubmissionCategoryId(submissionCategoryId);
    }

    public void saveSubmissionCategory(final SubmissionCategory category) {
        submissionCategoryRepository.save(category);
    }

    public Optional<SubmissionCategory> findSubmissionCategory(final long submissionCategoryId) {
        return submissionCategoryRepository.findById(submissionCategoryId);
    }

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

    public SortedMap<BigInteger, String> findUserPrivateSubmissions(UserPrincipal user, ChromatographyType type) {
        Iterable<Submission> submissions =
                submissionRepository.findByPrivateTrueAndReferenceTrueAndUserAndChromatographyType(user, type);

        SortedMap<BigInteger, String> submissionIdToNameMap = new TreeMap<>();
        for (Submission submission : submissions) {
            String html = String.format("%s <span class='badge badge-info'>private</span>%s",
                    submission.getName(),
                    submissionRepository.getIsInHouseReference(submission.getId()) ? " <span class='badge badge-success'>in-house</span>" : "");
            submissionIdToNameMap.put(BigInteger.valueOf(submission.getId()), html);
        }
        return submissionIdToNameMap;
    }

    public SortedMap<BigInteger, String> findPublicSubmissions(ChromatographyType type) {
        Iterable<Submission> submissions =
                submissionRepository.findByPrivateFalseAndReferenceTrueAndChromatographyType(type);

        SortedMap<BigInteger, String> submissionIdToNameMap = new TreeMap<>();
        submissions.forEach(s -> submissionIdToNameMap.put(BigInteger.valueOf(s.getId()), s.getName()));
        return submissionIdToNameMap;
    }

    public SortedMap<Long, String> findUserPrivateSubmissions(UserPrincipal user) {
        Iterable<Submission> submissions =
                submissionRepository.findByPrivateTrueAndReferenceTrueAndUser(user);

        SortedMap<Long, String> submissionIdToNameMap = new TreeMap<>();
        submissions.forEach(s -> submissionIdToNameMap.put(s.getId(), s.getName()));
        return submissionIdToNameMap;
    }

    public Map<Long, List<ChromatographyType>> findChromatographyTypes(List<Submission> submissions) {

        List<Long> submissionIds = submissions.stream()
                .map(Submission::getId)
                .collect(Collectors.toList());

        return MappingUtils.toMapOfLists(submissionIds.isEmpty()
                ? new ArrayList<>(0)
                : submissionRepository.findChromatographyTypesBySubmissionId(submissionIds));
    }

    public Map<Long, Boolean> getIdToIsLibraryMap(List<Submission> submissions) {
        long[] submissionIds = submissions.stream()
                .mapToLong(Submission::getId)
                .toArray();

        return MappingUtils.toMap(submissionIds.length == 0
                ? new ArrayList<>(0)
                : spectrumRepository.getAllSpectrumReferenceBySubmissionIds(submissionIds));
    }

    public Map<Long, Boolean> getIdToIsInHouseLibraryMap(List<Submission> submissions) {
        long[] submissionIds = submissions.stream()
                .mapToLong(Submission::getId)
                .toArray();

        return MappingUtils.toMap(submissionIds.length == 0
                ? new ArrayList<>(0)
                : spectrumRepository.getAllSpectrumInHouseReferenceBySubmissionIds(submissionIds));
    }

    public boolean isInHouseReference(Submission s) {
        s.setInHouse(submissionRepository.getIsInHouseReference(s.getId()));
        return s.getIsInHouse();
    }

    public boolean isLibrary(Submission s) {
        s.setIsLibrary(submissionRepository.getIsLibrary(s.getId()));
        return s.isLibrary();
    }

    public boolean isSearchable(Submission s){
        s.setSearchable(submissionRepository.getIsSearchable(s.getId()));
        return s.isSearchable();
    }

    public Iterable<Submission> findSubmissionByClusterableTrueAndConsensusFalseAndInHouseFalse(){
        return submissionRepository.findSubmissionByClusterableTrueAndConsensusFalseAndInHouseFalse();
    }

    public DataTableResponse findSubmissionsPagable(int start, int length, int column, String sortDirection) {

        //get column name that is sorted
        final String sortColumn = ColumnInformation.getColumnNameFromPosition(column);

        //get page format
        Pageable pageable = DataUtils.createPageable(start, length, sortColumn, sortDirection);

        // fetch x records at a time based on start page .
        Page<Submission>pagedResult = submissionRepository.findSubmissionByClusterableTrueAndConsensusFalseAndInHouseFalse(pageable);

        //create submission dto
        List<SubmissionDTO> submissionDTOList = new ArrayList<>();
        for(Submission s : pagedResult.getContent()) {
            submissionDTOList.add(new SubmissionDTO(s,s.isLibrary(),false, true));
        }
        final DataTableResponse response = new DataTableResponse(submissionDTOList);
        response.setRecordsTotal(pagedResult.getTotalElements());
        response.setRecordsFiltered(pagedResult.getTotalElements());

        return response;


    }
}
