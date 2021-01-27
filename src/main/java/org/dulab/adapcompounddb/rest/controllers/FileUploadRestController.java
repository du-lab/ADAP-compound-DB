package org.dulab.adapcompounddb.rest.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.FileType;
import org.dulab.adapcompounddb.models.enums.UserRole;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.controllers.utils.MultipartFileUtils;
import org.dulab.adapcompounddb.site.services.AuthenticationService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@RestController
public class FileUploadRestController {

    private static final Logger LOGGER = LogManager.getLogger(FileUploadRestController.class);

    private final AuthenticationService authenticationService;
    private final SubmissionService submissionService;
    private final Gson gson = new GsonBuilder().create();

    @Autowired
    public FileUploadRestController(AuthenticationService authenticationService, SubmissionService submissionService) {
        this.authenticationService = authenticationService;
        this.submissionService = submissionService;
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

            MultipartFileUtils.readMultipartFile(submission, files, fileType, chromatographyType);

            submissionService.saveSubmission(submission);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
