package org.dulab.adapcompounddb.site.services;

import org.apache.commons.collections.CollectionUtils;
import org.dulab.adapcompounddb.models.enums.SearchTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
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
    private static final Logger LOG = LoggerFactory.getLogger(SubmissionService.class);

    private static final double MEMORY_PER_PEAK = 1.3e-7; //in GB

    //    private static final String DESC = "DESC";
    private final SubmissionRepository submissionRepository;

    private final UserPrincipalRepository userPrincipalRepository;
    private final SubmissionTagRepository submissionTagRepository;
    private final SubmissionCategoryRepository submissionCategoryRepository;
    private final SpectrumRepository spectrumRepository;
    private final MultiFetchRepository multiFetchRepository;
    private final SearchTaskRepository searchTaskRepository;


    @Autowired
    public SubmissionService(final SubmissionRepository submissionRepository,
                             final UserPrincipalRepository userPrincipalRepository,
                             final SubmissionTagRepository submissionTagRepository,
                             final SubmissionCategoryRepository submissionCategoryRepository,
                             final SpectrumRepository spectrumRepository,
                             final MultiFetchRepository multiFetchRepository,
                             final SearchTaskRepository searchTaskRepository) {

        this.submissionRepository = submissionRepository;
        this.userPrincipalRepository = userPrincipalRepository;
        this.submissionTagRepository = submissionTagRepository;
        this.submissionCategoryRepository = submissionCategoryRepository;
        this.spectrumRepository = spectrumRepository;
        this.multiFetchRepository = multiFetchRepository;
        this.searchTaskRepository = searchTaskRepository;
    }

    @Transactional
    public Submission findSubmission(final long submissionId) {
        return submissionRepository.findById(submissionId).orElseThrow(EmptyStackException::new);
    }


    public Submission fetchSubmission(long submissionId) {
        return multiFetchRepository.getSubmissionWithFilesSpectraPeaksIsotopes(submissionId);
    }

    public Submission fetchSubmissionPartial(long submissionId) {
        return multiFetchRepository.getSubmissionWithFilesSpectra(submissionId);
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
                    submissionRepository.getAllReferenceBySubmissionIds(submissionIds));
            Map<Long, Boolean> inHouseReferences = MappingUtils.toMap(
                    submissionRepository.getAllInHouseReferenceBySubmissionIds(submissionIds));
            Map<Long, Boolean> clusterables = MappingUtils.toMap(
                    submissionRepository.getAllClusterableBySubmissionIds(submissionIds));

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
    public Submission saveSubmission(final Submission submission) {

        //only save submission if it doens't surpass peak capacity
        UserPrincipal user = submission.getUser();
        String userName = user.getUsername();
        int count = 0;
        if(!user.isAdmin()) {
            count = user.getPeakNumber();
            if (count == 0) {
                count = submissionRepository.getPeaksByUserName(user.getUsername());
            }

            //default peak capacity
            int peakCapacity = user.getPeakCapacity();
            if (count > peakCapacity) {
                throw new IllegalStateException("You have reached a limit of data allowed to store in ADAP-KDB. Before " +
                        "saving new data to ADAP-KDB, please delete some of your existing studies/libraries");
            }
        }

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
        calculateAndSavePeakNumber(submission, count, Submission.SAVE_SUBMISSION);

        SearchTask searchTask = new SearchTask();
        searchTask.setSubmission(submissionObj);
        searchTask.setUser(user);
        searchTask.setStatus(SearchTaskStatus.NOT_STARTED);
        searchTask.setDateTime(new Date());
        SearchTask s = searchTaskRepository.save(searchTask);
        return submissionObj;
    }

    private void calculateAndSavePeakNumber(final Submission submission, final int fetchedPeakNumber,
                                            final String operation) {
        UserPrincipal user = submission.getUser();
        int submissionPeakNumber = 0;
        try {
            for (File file : submission.getFiles()) {
                for (Spectrum spectrum : file.getSpectra()) {
                    submissionPeakNumber += spectrum.getPeaks().size();
                }
            }
            if (submissionPeakNumber > 0) {
                if (operation.equals(Submission.SAVE_SUBMISSION)) {
                    user.setPeakNumber(fetchedPeakNumber + submissionPeakNumber);
                } else if (operation.equals(Submission.DELETE_SUBMISSION)) {
                    user.setPeakNumber(Math.max(fetchedPeakNumber - submissionPeakNumber, 0));
                }
                userPrincipalRepository.save(user);
            }
        } catch (Exception e){
            LOG.error("Error while calculating and saving peak number for submission : " + submission.getId() + "" +
                    " for user : " + user.getId() + " : " + e);
        }
    }

    @Transactional
    public void deleteSubmission(final Submission submission) {
        submissionRepository.delete(submission);
    }

    @Transactional
    public void delete(final long submissionId) {
        Optional<Submission> submission = submissionRepository.findById(submissionId);
        if (submission.isPresent()) {
            UserPrincipal user = submission.get().getUser();
            submissionRepository.delete(submission.get());
//            PeakNumber here cannot be zero (if user has files) account page calculate PeakNumber and to delete we need
//            to go to account page, and account page calculates PeakNumber if user.peakNumber is zero
            calculateAndSavePeakNumber(submission.get(), user.getPeakNumber(), Submission.DELETE_SUBMISSION);
        }
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
        Iterable<Submission> submissions;
        if (user != null && user.getOrganizationId() !=  null) {
            submissions = submissionRepository
                    .findByPrivateTrueAndReferenceTrueAndUserOrOrgAndChromatographyType(user, user.getOrganizationUser(), type);
        } else {
            submissions = submissionRepository.findByPrivateTrueAndReferenceTrueAndUserAndChromatographyType(user, type);
        }


        SortedMap<BigInteger, String> submissionIdToNameMap = new TreeMap<>();
        for (Submission submission : submissions) {
            String badgeType = "info", submissionType = "private";
            if (submission.getUser().getId() != user.getId()) {
                badgeType = "warning";
                submissionType = "organization";
            }
            String html = String.format("%s <span class='badge badge-"+badgeType+"'>"+submissionType+"</span>%s",
                    submission.getName(),
                    submission.isInHouseReference() ? " <span class='badge badge-success'>in-house</span>" : "");
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


    public boolean isSearchable(Submission s){
        s.setSearchable(submissionRepository.getIsSearchable(s.getId()));
        return s.isSearchable();
    }

//    public Iterable<Submission> findSubmissionByClusterableTrueAndConsensusFalseAndInHouseFalse(){
//        return submissionRepository.findSubmissionByClusterableTrueAndConsensusFalseAndInHouseFalse();
//    }

    public DataTableResponse findSubmissionsPagable(int start, int length, int column, String sortDirection) {

        //get column name that is sorted
        final String sortColumn = ColumnInformation.getColumnNameFromPosition(column);

        //get page format
        Pageable pageable = DataUtils.createPageable(start, length, sortColumn, sortDirection);

        // fetch x records at a time based on start page .
        Page<Submission>pagedResult = submissionRepository.findSubmissionByClusterableTrue(pageable);

        //create submission dto
        List<SubmissionDTO> submissionDTOList = new ArrayList<>();
        for(Submission s : pagedResult.getContent()) {
            submissionDTOList.add(new SubmissionDTO(s,s.getIsReference(),false, true));
        }
        final DataTableResponse response = new DataTableResponse(submissionDTOList);
        response.setRecordsTotal(pagedResult.getTotalElements());
        response.setRecordsFiltered(pagedResult.getTotalElements());

        return response;


    }

    public double getPeakDiskSpaceByUser(UserPrincipal user) {
        int peakPerUser = user.getPeakNumber();
        if (peakPerUser == 0) {
            peakPerUser = submissionRepository.getPeaksByUserName(user.getUsername());
            if (peakPerUser > 0) {
                user.setPeakNumber(peakPerUser);
                userPrincipalRepository.save(user);
            }
        }
        return peakPerUser * MEMORY_PER_PEAK;

    }

    @Transactional
    public void updateReferenceBySubmissionId(long submissionId, boolean value) {
        submissionRepository.updateReferenceBySubmissionId(submissionId, value);
    }
}
