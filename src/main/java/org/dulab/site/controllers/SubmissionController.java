package org.dulab.site.controllers;

import org.dulab.models.*;
import org.dulab.site.services.*;
import org.dulab.validation.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
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

    private final SubmissionService submissionService;

    private final SpectrumService spectrumService;

    @Autowired
    public SubmissionController(SubmissionService submissionService,
                                SpectrumService spectrumService) {

        this.submissionService = submissionService;
        this.spectrumService = spectrumService;
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
            return new ModelAndView("");

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
            return new ModelAndView("");
        }

        model.addAttribute("message", "Mass spectra are submitted successfully.");
        Submission.clear(session);
        return new ModelAndView(new RedirectView("/submission/" + submission.getId() + "/", true));
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

    @RequestMapping(value = "{submissionId:\\d+}/delete/")
    public View delete(HttpServletRequest request, @PathVariable("submissionId") long id) {

        Submission submission = submissionService
                .findSubmission(id)
                .orElseThrow(() -> new IllegalStateException("Submission with Id = " + id + " does not exist."));

        submissionService.deleteSubmission(submission);
        return new RedirectView("/account/", true);
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


    @RequestMapping(value = "{submissionId:\\d+}/{spectrumId:\\d+}/match/", method = RequestMethod.GET)
    public String match(@PathVariable("submissionId") long submissionId,
                        @PathVariable("spectrumId") int spectrumId,
                        HttpSession session,
                        Model model) {

        Submission submission = getSubmission(submissionId, session);
        Spectrum querySpectrum = submission.getSpectra().get(spectrumId);

        List<Hit> hits = spectrumService.match(querySpectrum);

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("hits", hits);

        return "file/match";
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
        return submissionService
                .findSubmission(id)
                .orElse(Submission.from(session));
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
