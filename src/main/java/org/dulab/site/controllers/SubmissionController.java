package org.dulab.site.controllers;

import org.dulab.models.*;
import org.dulab.site.services.DefaultSpectrumService;
import org.dulab.site.services.SpectrumService;
import org.dulab.site.services.DefaultSubmissionService;
import org.dulab.site.services.SubmissionService;
import org.dulab.validation.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("submission/")
public class SubmissionController {

    private SubmissionService submissionService;
    private SpectrumService spectrumService;

    public SubmissionController() {
        submissionService = new DefaultSubmissionService();
        spectrumService = new DefaultSpectrumService();
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public View view() {
        return new RedirectView("0/");
    }

    @RequestMapping(value = "{submissionId:\\d+}/", method = RequestMethod.GET)
    public String viewSubmission(HttpSession session, Model model,
                                 @PathVariable("submissionId") long submissionId) {

        Submission submission = getSubmission(submissionId, session);
        if (submission == null)
            return "file/submissionnotfound";

        model.addAttribute("submission", submission);

        Form form = new Form();
        form.setName(submission.getName());
        form.setDescription(submission.getDescription());
        model.addAttribute("form", form);

        return "file/view";
    }

    @RequestMapping(value = "{submissionId:\\d+}/", method = RequestMethod.POST)
    public ModelAndView submit(HttpSession session, Model model,
                               @PathVariable("submissionId") long submissionId,
                               @Valid Form form, Errors errors) {

        if (errors.hasErrors())
            return new ModelAndView("file/view");

        Submission submission = getSubmission(submissionId, session);
        submission.setName(form.getName());
        submission.setDescription(form.getDescription());
        submission.setDateTime(new Date());
        submission.setUser(UserPrincipal.from(session));

        try {
            submissionService.saveSubmission(submission);
        }
        catch (ConstraintViolationException e) {
            model.addAttribute("validationErrors", e.getConstraintViolations());
            return new ModelAndView("file/view");
        }

        model.addAttribute("message", "Mass spectra are submitted successfully.");
        Submission.clear(session);
        return new ModelAndView(new RedirectView(submission.getId() + "/"));
    }

    @RequestMapping(value = "{submissionId:\\d+}/fileview/", method = RequestMethod.GET)
    public void rawView(HttpSession session, HttpServletResponse response,
                        @PathVariable("submissionId") long id) throws IOException {

        Submission submission = getSubmission(id, session);
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "inline; filename=\"" + submission.getFilename() + "\"");
        response.getOutputStream().write(submission.getFile());
    }

    @RequestMapping(value = "{submissionId:\\d+}/filedownload/", method = RequestMethod.GET)
    public void rawDownload(HttpSession session, HttpServletResponse response,
                            @PathVariable("submissionId") long id) throws IOException {

        Submission submission = getSubmission(id, session);
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + submission.getFilename() + "\"");
        response.getOutputStream().write(submission.getFile());
    }

    @RequestMapping(value = "{submissionId:\\d+}/{spectrumId:\\d+}/", method = RequestMethod.GET)
    public String spectrum(@PathVariable("submissionId") long submissionId,
                           @PathVariable("spectrumId") int spectrumId,
                           HttpSession session,
                           Model model) {

        Submission submission = getSubmission(submissionId, session);
        Spectrum spectrum = submission.getSpectra().get(spectrumId);

        model.addAttribute("name", spectrum.toString());
        model.addAttribute("properties", spectrum.getProperties());
        model.addAttribute("jsonPeaks", peaksToJson(spectrum.getPeaks()));

        return "file/spectrum";
    }


    @RequestMapping(value = "file/view/{submissionId:\\d+}/{spectrumId:\\d+}/match/", method = RequestMethod.GET)
    public String match(@PathVariable("submissionId") long submissionId,
                        @PathVariable("spectrumId") int spectrumId,
                        HttpSession session,
                        Model model) {

        Submission submission = getSubmission(submissionId, session);
        Spectrum querySpectrum = submission.getSpectra().get(spectrumId);

        List<Hit> hits = spectrumService.match(querySpectrum);

        return null;
    }

    private String peaksToJson(List<Peak> peaks) {

        double maxIntensity = peaks.stream()
                .mapToDouble(Peak::getIntensity)
                .max()
                .orElseThrow(() -> new IllegalStateException("Cannot determine maximum intensity of the peak list"));

        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < peaks.size(); ++i) {
            if (i != 0)
                stringBuilder.append(',');
            stringBuilder.append('[')
                    .append(peaks.get(i).getMz())
                    .append(',')
                    .append(100 * peaks.get(i).getIntensity() / maxIntensity)
                    .append(']');
        }
        stringBuilder.append(']');

        return stringBuilder.toString();
    }

    private Submission getSubmission(long id, HttpSession session) {
        if (id > 0)
            return submissionService.findSubmission(id);
        else
            return Submission.from(session);
    }

    public static class Form {

        @NotBlank(message = "The field Name is required.")
        private String name;

        @NotBlank(message = "The field Description is required.")
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
