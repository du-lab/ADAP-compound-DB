package org.dulab.site.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.models.ChromatographyType;
import org.dulab.models.FileType;
import org.dulab.models.entities.Spectrum;
import org.dulab.models.entities.Submission;
import org.dulab.site.services.FileReaderService;
import org.dulab.site.services.MspFileReaderService;
import org.dulab.validation.ContainsFiles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FileUploadController {

    private static final Logger LOG = LogManager.getLogger();

    private Map<FileType, FileReaderService> fileReaderServiceMap;

    public FileUploadController() {
        fileReaderServiceMap = new HashMap<>();
        fileReaderServiceMap.put(FileType.MSP, new MspFileReaderService());
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("chromatographyTypeList", ChromatographyType.values());
        model.addAttribute("fileTypeList", FileType.values());
    }

    @RequestMapping(value = "/file/upload/", method = RequestMethod.GET)
    public String upload(Model model, HttpSession session) {
        if (Submission.from(session) != null)
            return "redirect:/file/";

        FileUploadForm form = new FileUploadForm();
        form.setFileType(FileType.MSP);
        model.addAttribute("form", form);
        return "file/upload";
    }

    @RequestMapping(value = "/file/upload/", method = RequestMethod.POST, consumes = "multipart/form-data")
    public String upload(Model model, HttpSession session, @Valid FileUploadForm form, Errors errors) {

        if (Submission.from(session) != null)
            return "redirect:/file/";

        if (errors.hasErrors())
            return "file/upload";

        MultipartFile file = form.getFile();
        Submission submission = new Submission();
        submission.setName(file.getOriginalFilename());
        submission.setFilename(file.getOriginalFilename());
        submission.setFileType(form.getFileType());
        submission.setChromatographyType(form.getChromatographyType());

        FileReaderService service = fileReaderServiceMap.get(form.fileType);
        if (service == null) {
            LOG.warn("Cannot find an implementation of FileReaderService for a file of type {}", form.getFileType());
            model.addAttribute("message", "Cannot read this type.");
            return "file/upload";
        }

        try {
            submission.setFile(file.getBytes());
            submission.setSpectra(service.read(file.getInputStream()));
            for (Spectrum s : submission.getSpectra())
                s.setSubmission(submission);
        }
        catch (IOException e) {
            LOG.warn(e);
            model.addAttribute("message", "Cannot read this file: " + e.getMessage());
            return "file/upload";
        }

        if (submission.getSpectra() == null || submission.getSpectra().isEmpty()) {
            model.addAttribute("message", "Cannot read this file");
            return "file/upload";
        }

        Submission.assign(session, submission);
        return "redirect:/file/";
    }


    private static class FileUploadForm {

        @NotNull(message = "Chromatography type must be selected.")
        private ChromatographyType chromatographyType;

        @NotNull(message = "File format must be chosen.")
        private FileType fileType;

        @ContainsFiles
        private MultipartFile file;

        public ChromatographyType getChromatographyType() {
            return chromatographyType;
        }

        public void setChromatographyType(ChromatographyType chromatographyType) {
            this.chromatographyType = chromatographyType;
        }

        public FileType getFileType() {
            return fileType;
        }

        public void setFileType(FileType fileType) {
            this.fileType = fileType;
        }

        public MultipartFile getFile() {
            return file;
        }

        public void setFile(MultipartFile file) {
            this.file = file;
        }
    }
}
