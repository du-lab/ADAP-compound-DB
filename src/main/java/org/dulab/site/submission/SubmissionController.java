package org.dulab.site.submission;

import org.dulab.site.models.Spectrum;
import org.dulab.site.models.Submission;
import org.dulab.site.services.FileReaderService;
import org.dulab.site.services.MspFileReaderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class SubmissionController {

    private FileReaderService fileReaderService;

    public SubmissionController() {
        fileReaderService = new MspFileReaderService();
    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public ModelAndView submission(HttpSession session) {
        if (Submission.getSubmission(session) == null)
            return new ModelAndView(new RedirectView("/file/upload", true, false));

        return new ModelAndView("file/view");
    }

    @RequestMapping(value = "/file/upload", method = RequestMethod.GET)
    public ModelAndView upload(Model model, HttpSession session) {
        if (Submission.getSubmission(session) != null)
            return new ModelAndView(new RedirectView("/file/view", true, false));

        model.addAttribute("chromatographyTypes", Submission.ChromatographyType.values());
        return new ModelAndView("file/upload");
    }

    @RequestMapping(value = "file/upload", method = RequestMethod.POST, consumes = {"multipart/form-data")
    public ModelAndView upload(Model model, HttpSession session, @RequestParam("file") MultipartFile file) {
        if (file.getSize() == 0) {
            model.addAttribute("message", "Uploaded file is empty");
            return new ModelAndView()new RedirectView("/file/upload", true, false));
        }

        Submission submission = new Submission();

        List<Spectrum> spectra = fileReaderService.read(file.getInputStream());
        if (spectra == null || spectra.isEmpty()) {
            model.addAttribute("message", "Cannot read this file");
            return new RedirectView("/file/upload");
        }

        session.setAttribute("spectrumList", spectra);
        session.setAttribute("fileName", file.getOriginalFilename());
        return new RedirectView("/file");
    }


    private static class Form {

        private Submission.ChromatographyType chromatographyType;
        private MultipartFile file;

        public Submission.ChromatographyType getChromatographyType() {
            return chromatographyType;
        }

        public void setChromatographyType(Submission.ChromatographyType chromatographyType) {
            this.chromatographyType = chromatographyType;
        }

        public MultipartFile getFile() {
            return file;
        }

        public void setFile(MultipartFile file) {
            this.file = file;
        }
    }
}
