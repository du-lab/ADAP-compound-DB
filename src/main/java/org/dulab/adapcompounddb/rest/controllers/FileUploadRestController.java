package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.User;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.dulab.adapcompounddb.site.services.utils.GroupSearchStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.models.MetaDataMapping.Field;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.FileType;
import org.dulab.adapcompounddb.models.enums.UserRole;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.controllers.utils.MultipartFileUtils;
import org.dulab.adapcompounddb.site.services.AuthenticationService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class FileUploadRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadRestController.class);

    private final AuthenticationService authenticationService;
    private final SubmissionService submissionService;
    private final UserPrincipalService userPrincipalService;
    private final GroupSearchService groupSearchService;
    private final GroupSearchStorageService groupSearchStorageService;
    private final Gson gson = new GsonBuilder().create();

    @Autowired
    public FileUploadRestController(AuthenticationService authenticationService, SubmissionService submissionService,
                                    GroupSearchService groupSearchService, UserPrincipalService userPrincipalService, GroupSearchStorageService groupSearchStorageService) {
        this.authenticationService = authenticationService;
        this.submissionService = submissionService;
        this.groupSearchService = groupSearchService;
        this.userPrincipalService = userPrincipalService;
        this.groupSearchStorageService = groupSearchStorageService;
    }

    @RequestMapping(value = "/rest/fileupload/", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public void createSubmission(@ModelAttribute("username") String userName,
                                 @ModelAttribute("password") String password,
                                 @ModelAttribute("file-type") String fileTypeString,
                                 @ModelAttribute("chromatography") String chromatographyString,
                                 @ModelAttribute("json") String json,
                                 @RequestPart("files") @NotNull @NotBlank List<MultipartFile> files
    ) {

        try {
            UserPrincipal user = authenticationService.authenticate(userName, password);
            if (user == null || !user.getRoles().contains(UserRole.ADMIN))
                throw new IllegalStateException(String.format(
                        "User %s is not authorized to use REST API or the password doesn't match", userName));

            FileType fileType = FileType.valueOf(fileTypeString);
            ChromatographyType chromatographyType = ChromatographyType.valueOf(chromatographyString);

            Submission submission = gson.fromJson(json, Submission.class);
            submission.setUser(user);
            submission.setDateTime(new Date());
            submission.getTags().forEach(tag -> tag.setSubmission(submission));

            MetaDataMapping metaDataMapping = new MetaDataMapping();
            metaDataMapping.setFieldName(Field.NAME, "Name");
            metaDataMapping.setFieldName(Field.MASS, "Mass");
            metaDataMapping.setFieldName(Field.FORMULA, "Formula");
            //TODO Replace Field.EXTENRAL_ID with Field.CAS_ID
            //TODO Map "NIST Id" to Field.EXTENRAL_ID
            metaDataMapping.setFieldName(Field.CAS_ID, "CASNO");
            metaDataMapping.setFieldName(Field.EXTERNAL_ID, "NIST Id");
            metaDataMapping.setFieldName(Field.INCHI_KEY, "INCHI_KEY");
            metaDataMapping.setFieldName(Field.RETENTION_TIME, "RT");
            metaDataMapping.setFieldName(Field.PRECURSOR_MZ, "PrecursorMz");
            metaDataMapping.setFieldName(Field.PRECURSOR_TYPE, "Precursor_type");

            Map<FileType, MetaDataMapping> metaDataMappingMap = new HashMap<>();
            metaDataMappingMap.put(FileType.MSP, metaDataMapping);

            MultipartFileUtils.readMultipartFile(submission, files, chromatographyType, metaDataMappingMap,
                    false, false);

            submissionService.saveSubmission(submission);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    //api to get user's libraries
    @GetMapping(value="/rest/libraries")
    public Map<Long, String> getLibraries(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<Long, String> librariesToIds = new HashMap<>();
        List<Submission> submissions = (List<Submission>) submissionService.findAllPublicLibraries();
        //return public and private lirbaries if user is authenticated
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();
            UserPrincipal userPrincipal = userPrincipalService.findUserByUsername(username);
            List<Submission> userSubmissions = submissionService.findSubmissionsByUserId(userPrincipal.getId());
            for(Submission submission : submissions){
                librariesToIds.put(submission.getId(), submission.getName());
            }
            for(Submission userSubmission : userSubmissions){
                librariesToIds.put(userSubmission.getId(), userSubmission.getName());
            }
        }
        //return public lib if user is not authenticated
        else{
            for(Submission s : submissions){
                librariesToIds.put(s.getId(), s.getName());
            }
        }
        return librariesToIds;
    }
    //get progress of group search
    @GetMapping(value="/rest/groupsearch/progress")
    public ResponseEntity<Integer> getProgress(@RequestParam("jobId") String jobId) {
        Integer progress = groupSearchStorageService.getProgress(jobId);
        if (progress != null) {
            return ResponseEntity.ok(progress);
        } else {
            //job id not found
            return ResponseEntity.notFound().build();
        }
    }
    //get group search results
    @GetMapping(value = "/rest/groupsearch/results")
    public ResponseEntity<Map<String,Object>> getGroupSearchResults(@RequestParam("jobId") String jobId) {
        Map<String,Object> results = groupSearchStorageService.getResults(jobId);

        if (results != null) {
            //TODO: clear the storage
            return ResponseEntity.ok(results);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //run group search
    @RequestMapping(value = "/rest/groupsearch/", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public void startGroupSearch(@RequestPart("files") @NotNull @NotBlank List<MultipartFile> files,
                                 @RequestParam("libraryIds") String libraryIdsJson, //libraries to search agianst
                                 @RequestParam("withOntologyLevel") String withOntologyLevelString,
                                 @RequestParam("chromatographyString") String chromatographyString,
                                 @RequestParam("jobId") String jobId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<BigInteger> libraryIds;
        try {

            if(authentication.isAuthenticated()) {
                String username = authentication.getName();
                UserPrincipal userPrincipal = userPrincipalService.findUserByUsername(username);


                Submission submission = new Submission();
                submission.setUser(userPrincipal);
                submission.setDateTime(new Date());
                ChromatographyType chromatographyType = ChromatographyType.valueOf(chromatographyString);
                MetaDataMapping metaDataMapping = new MetaDataMapping();
                metaDataMapping.setFieldName(Field.NAME, "Name");
                metaDataMapping.setFieldName(Field.MASS, "Mass");
                metaDataMapping.setFieldName(Field.FORMULA, "Formula");
                //TODO Replace Field.EXTENRAL_ID with Field.CAS_ID
                //TODO Map "NIST Id" to Field.EXTENRAL_ID
                metaDataMapping.setFieldName(Field.CAS_ID, "CASNO");
                metaDataMapping.setFieldName(Field.EXTERNAL_ID, "NIST Id");
                metaDataMapping.setFieldName(Field.INCHI_KEY, "INCHI_KEY");
                metaDataMapping.setFieldName(Field.RETENTION_TIME, "RT");
                metaDataMapping.setFieldName(Field.PRECURSOR_MZ, "PrecursorMz");
                metaDataMapping.setFieldName(Field.PRECURSOR_TYPE, "Precursor_type");

                Map<FileType, MetaDataMapping> metaDataMappingMap = new HashMap<>();
                metaDataMappingMap.put(FileType.MSP, metaDataMapping);

                MultipartFileUtils.readMultipartFile(submission, files, chromatographyType, metaDataMappingMap,
                        false, false);

                libraryIds = Arrays.stream(libraryIdsJson.split(",")).map(BigInteger::new).collect(Collectors.toSet());
                boolean withOntology = Boolean.parseBoolean(withOntologyLevelString) ;
                groupSearchService.groupSearch(userPrincipal,submission.getFiles(),libraryIds,withOntology,jobId);
            }

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}