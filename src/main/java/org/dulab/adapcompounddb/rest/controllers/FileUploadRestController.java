package org.dulab.adapcompounddb.rest.controllers;

import com.amazonaws.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.User;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public List<Map<String, Object>> getLibraries(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Map<String, Object>> libraries = new ArrayList<>();
        List<Submission> submissions = (List<Submission>) submissionService.findAllPublicLibraries();

        //all public libraries
        for(Submission submission : submissions){
            Map<String, Object> library = new HashMap<>();
            library.put("id", submission.getId());
            library.put("name", submission.getName());
            library.put("chromatographyType", submission.getChromatographyType());
            libraries.add(library);
        }
        //private if user is authenticated
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();
            UserPrincipal userPrincipal = userPrincipalService.findUserByUsername(username);

            long id = userPrincipal.getOrganizationId() != null ? userPrincipal.getOrganizationId() : userPrincipal.getId();
            List<Submission> userSubmissions = submissionService.findPrivateSubmissionByUserId(id);

            for(Submission userSubmission : userSubmissions){
                Map<String, Object> library = new HashMap<>();
                library.put("id", userSubmission.getId());
                library.put("name", userSubmission.getName());
                library.put("chromatographyType", userSubmission.getChromatographyType());
                libraries.add(library);
            }
        }

        return libraries;
    }
    //get progress of group search
    @GetMapping(value="/rest/groupsearch/progress")
    public ResponseEntity<Double> getProgress(@RequestParam("jobId") String jobId) {
        Double progress = groupSearchStorageService.getProgress(jobId);
        if (progress != null) {
            return ResponseEntity.ok(progress);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //get group search results
    @GetMapping(value = "/rest/groupsearch/results")
    public ResponseEntity<?> getGroupSearchResults(@RequestParam("jobId") String jobId) throws IOException {
        Map<String,Object> results = groupSearchStorageService.getResults(jobId);

        if (results != null) {
            //clear storage
            groupSearchStorageService.clear(jobId);

            ObjectMapper objectMapper = new ObjectMapper();
            byte[] jsonData = objectMapper.writeValueAsBytes(results);
            //generate zip file
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ZipOutputStream zos = new ZipOutputStream(baos)) {
                ZipEntry entry = new ZipEntry(jobId + ".json");
                zos.putNextEntry(entry);
                zos.write(jsonData);
                zos.closeEntry();

                ByteArrayResource byteArrayResource = new ByteArrayResource(baos.toByteArray());

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + jobId + ".zip");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(byteArrayResource.contentLength())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(byteArrayResource);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //run group search
    @RequestMapping(value = "/rest/groupsearch/", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<?> startGroupSearch(@RequestPart("files") @NotNull @NotBlank List<MultipartFile> files,
                                      @RequestParam("libraryIds") String libraryIdsJson, //libraries to search agians
                                      @RequestParam("withOntologyLevel") String withOntologyLevelString,
                                      @RequestParam("chromatographyString") String chromatographyString,
                                      @RequestParam("scoreThreshold") String scoreThreshold,
                                      @RequestParam("retentionIndexTolerance") String retentionIndexTolerance,
                                      @RequestParam("retentionIndexMatch") String retentionIndexMatch,
                                      @RequestParam("mzTolerance") String mzTolerance,
                                      @RequestParam("mzToleranceType") String mzToleranceType,
                                      @RequestParam("matchesPerSpectrum") String matchesPerSpectrum,
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
                Map<BigInteger, String> libraryIdMap = new HashMap<>();
                libraryIds.forEach(id -> libraryIdMap.put(id, null));
                boolean withOntology = Boolean.parseBoolean(withOntologyLevelString) ;

                //other parameters
                SearchParameters parameters = new SearchParameters();
                parameters.setScoreThreshold(scoreThreshold != null ? Double.parseDouble(scoreThreshold) / 1000.0 : null);
                parameters.setRetIndexTolerance(Double.parseDouble(retentionIndexTolerance));
                SearchParameters.RetIndexMatchType matchType;
                switch (retentionIndexMatch) {
                    case "Ignore Retention Index":
                        matchType = SearchParameters.RetIndexMatchType.IGNORE_MATCH;
                        break;
                    case "Penalize matches without Retention Index (Strong)":
                        matchType = SearchParameters.RetIndexMatchType.PENALIZE_NO_MATCH_STRONG;
                        break;
                    case "Penalize matches without Retention Index (Average)":
                        matchType = SearchParameters.RetIndexMatchType.PENALIZE_NO_MATCH_AVERAGE;
                        break;
                    case "Penalize matches without Retention Index (Weak)":
                        matchType = SearchParameters.RetIndexMatchType.PENALIZE_NO_MATCH_WEAK;
                        break;
                    case "Always match Retention Index":
                        matchType = SearchParameters.RetIndexMatchType.ALWAYS_MATCH;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid retention index match description: " + retentionIndexMatch);
                }
                parameters.setRetIndexMatchType(matchType);
                parameters.setMzTolerance(Double.parseDouble(mzTolerance), SearchParameters.MzToleranceType.valueOf(mzToleranceType.toUpperCase()) );
                parameters.setMZToleranceType(SearchParameters.MzToleranceType.valueOf(mzToleranceType.toUpperCase()));
                parameters.setLimit(Integer.parseInt(matchesPerSpectrum));
//                parameters.setSpecies(species);
//                parameters.setSource(source);
//                parameters.setDisease(disease);
                parameters.setSubmissionIds(libraryIds);
                Future<Void> asyncResult = groupSearchService.groupSearch(userPrincipal,null, null, submission.getFiles(),
                        null, parameters, libraryIdMap, withOntology, false, false, jobId);
                groupSearchStorageService.storeSearchJob(jobId, asyncResult);
            }

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/auth/", method = RequestMethod.POST)
    public ResponseEntity<?> validateCredentials(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            if (authentication.isAuthenticated()) {
                return ResponseEntity.ok("Login and password are correct!");
            } else
                return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @RequestMapping(value="/rest/group_search/cancel",method = RequestMethod.POST)
    public ResponseEntity<?> cancelGroupSearch(@RequestParam("jobId") String jobId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            boolean isCancelled = groupSearchStorageService.cancelSearchJob(jobId);
            if (isCancelled) {
                LOGGER.info("Group search canceled through rest api for job ID: " + jobId);
                return ResponseEntity.ok("Group search cancelled successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to cancel group search");
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}