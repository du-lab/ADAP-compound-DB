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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FileUploadController {

    private static final String META_FIELDS_COOKIE_NAME = "metaFields";

    private static final Logger LOG = LogManager.getLogger();

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

        if (errors.hasErrors()) {
            return "submission/upload";
        }

        Submission submission = new Submission();
        try {
            MultipartFileUtils.readMultipartFile(submission, form.getFiles(), form.getChromatographyType(),
                    form.getMetaDataMappings(), form.isMergeFiles());
        } catch (IllegalStateException e) {
            LOG.warn(e.getMessage(), e);
            model.addAttribute("message", e.getMessage());
            return "submission/upload";
        }

        Submission.assign(session, submission);

        byte[] jsonBytes = form.toJsonBytes();
        Cookie metaFieldsCookie = new Cookie(META_FIELDS_COOKIE_NAME, Base64.getEncoder().encodeToString(jsonBytes));
        response.addCookie(metaFieldsCookie);

        return "redirect:/file/";
    }
}
