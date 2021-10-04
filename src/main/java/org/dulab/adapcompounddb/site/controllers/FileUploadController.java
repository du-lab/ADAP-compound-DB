package org.dulab.adapcompounddb.site.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.FileType;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.controllers.forms.FileUploadForm;
import org.dulab.adapcompounddb.site.controllers.utils.MultipartFileUtils;
import org.dulab.adapcompounddb.site.services.io.FileReaderService;
import org.dulab.adapcompounddb.site.services.io.MspFileReaderService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FileUploadController {

    private static final String META_FIELDS_COOKIE_NAME = "metaFields";

    private static final Logger LOG = LogManager.getLogger(FileUploadController.class);

    private final Map<FileType, FileReaderService> fileReaderServiceMap;

    public FileUploadController() {
        fileReaderServiceMap = new HashMap<>();
        fileReaderServiceMap.put(FileType.MSP, new MspFileReaderService());
    }

    @ModelAttribute
    public void addAttributes(final Model model) {
        model.addAttribute("chromatographyTypeList", ChromatographyType.values());
        model.addAttribute("fileTypeList", FileType.values());
    }

    @RequestMapping(value = "/file/upload/nmdr", method = RequestMethod.GET)
    public String uploadFromNMDR(@RequestParam String archive, @RequestParam String file,
                                 @RequestParam String chromatography,
                                 Model model, HttpSession session, HttpServletResponse httpServletResponse) {

        ChromatographyType chromatographyType = null;
        chromatography = chromatography.toLowerCase();
        for (ChromatographyType type : ChromatographyType.values()) {
            if (type.name().toLowerCase().startsWith(chromatography)) {
                chromatographyType = type;
                break;
            }
        }

        if (chromatographyType == null)
            throw new IllegalStateException(
                    String.format("Chromatography of type %s is not supported", chromatography));

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("www.metabolomicsworkbench.org")
                .path("/data/file_extract_7z.php")
                .query(String.format("A=%s", archive))
                .query(String.format("F=%s", file))
                .build().encode();

        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(uriComponents.toUriString()).openStream())) {

            MultipartFile multipartFile = new MockMultipartFile(file, file, "multipart/form-data", inputStream);

            FileUploadForm fileUploadForm = new FileUploadForm();
            fileUploadForm.setChromatographyType(chromatographyType);
            fileUploadForm.setFiles(Collections.singletonList(multipartFile));

            Submission.clear(session);
            return upload(model, session, fileUploadForm, null, httpServletResponse);

        } catch (IOException e) {
            throw new IllegalStateException(String.format("Cannot read NMDR file %s from archive %s", archive, file), e);
        }
    }

    @RequestMapping(value = "/file/upload/", method = RequestMethod.GET)
    public String upload(Model model, HttpSession session,
                         @CookieValue(value = META_FIELDS_COOKIE_NAME, defaultValue = "") String metaFieldsInJson) {

        if (Submission.from(session) != null) {
            return "redirect:/file/";
        }

        FileUploadForm form;
        try {
            byte[] jsonBytes = Base64.getDecoder().decode(metaFieldsInJson);
            form = FileUploadForm.fromJsonBytes(jsonBytes);
        } catch (IOException e) {
            form = new FileUploadForm();
        }

        model.addAttribute("fileUploadForm", form);
        return "submission/upload";
    }

    @RequestMapping(value = "/file/upload/", method = RequestMethod.POST, consumes = "multipart/form-data")
    public String upload(Model model, HttpSession session, @Valid FileUploadForm form, Errors errors,
                         HttpServletResponse response) {

        if (Submission.from(session) != null) {
            return "redirect:/file/";
        }

        if (errors != null && errors.hasErrors()) {
            return "submission/upload";
        }

        Submission submission = new Submission();
//        try {
        MultipartFileUtils.readMultipartFile(submission, form.getFiles(), form.getChromatographyType(),
                form.getMetaDataMappings(), form.isMergeFiles());
//        } catch (IllegalStateException e) {
//            LOG.warn(e.getMessage(), e);
//            model.addAttribute("message", e.getMessage());
//            return "submission/upload";
//        }

        Submission.assign(session, submission);

        byte[] jsonBytes = form.toJsonBytes();
        Cookie metaFieldsCookie = new Cookie(META_FIELDS_COOKIE_NAME, Base64.getEncoder().encodeToString(jsonBytes));
        response.addCookie(metaFieldsCookie);

        return "redirect:/file/";
    }
}
