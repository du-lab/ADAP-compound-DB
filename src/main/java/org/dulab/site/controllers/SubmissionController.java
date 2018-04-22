package org.dulab.site.controllers;

import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.*;
import org.dulab.site.services.*;
import org.dulab.validation.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@ControllerAdvice
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

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("submissionCategories", submissionService.getAllSubmissionCategories());
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
        form.setSubmissionCategoryId(submission.getCategory() == null ? 0 : submission.getCategory().getId());
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
        submission.setCategory(submissionService
                .getSubmissionCategory(form.getSubmissionCategoryId())
                .orElse(null));

        try {
            submissionService.saveSubmission(submission);
        }
        catch (ConstraintViolationException e) {
            model.addAttribute("validationErrors", e.getConstraintViolations());
            return new ModelAndView("file/view");
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

        model.addAttribute("spectrum", spectrum);

        return "file/spectrum";
    }


    @RequestMapping(value = "{submissionId:\\d+}/{spectrumId:\\d+}/match/", method = RequestMethod.GET)
    public String matchGet(@PathVariable("submissionId") long submissionId,
                        @PathVariable("spectrumId") int spectrumId,
                        HttpSession session,
                        Model model) {
        Submission submission = getSubmission(submissionId, session);
        Spectrum querySpectrum = submission.getSpectra().get(spectrumId);

        UserParameters userParameters = new UserParameters();

        SpectrumSearchForm form = new SpectrumSearchForm();
        form.fromUserParameters(userParameters);

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("form", form);

        return "file/match";
    }

    @RequestMapping(value = "{submissionId:\\d+}/{spectrumId:\\d+}/match/", method = RequestMethod.POST)
    public String matchPost(@PathVariable("submissionId") long submissionId,
                            @PathVariable("spectrumId") int spectrumId,
                            HttpSession session, Model model,
                            @Valid SpectrumSearchForm form, Errors errors) {

        Submission submission = getSubmission(submissionId, session);
        Spectrum querySpectrum = submission.getSpectra().get(spectrumId);

        UserParameters userParameters = new UserParameters();
        form.toUserParameters(userParameters);

        try {
            List<Hit> hits = spectrumService.match(querySpectrum, userParameters);
            model.addAttribute("hits", hits);
        }
        catch (EmptySearchResultException e) {
            model.addAttribute("searchResultMessage", e.getMessage());
        }

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("form", form);

        return "file/match";
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

        private long submissionCategoryId;

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

        public long getSubmissionCategoryId() {
            return submissionCategoryId;
        }

        public void setSubmissionCategoryId(long submissionCategoryId) {
            this.submissionCategoryId = submissionCategoryId;
        }
    }


    public static class SpectrumSearchForm {

        @Min(value = 0, message = "M/z tolerance must be positive.")
        private float mzTolerance;

        @Min(value = 1, message = "Maximum number of hits must be greater than or equal to one.")
        private int numHits;

        @Min(value = 0, message = "Matching score threshold must be between 0 and 1000.")
        @Max(value = 1000, message = "Matching score threshold must be between 0 and 1000.")
        private int scoreThreshold;

        public float getMzTolerance() {
            return mzTolerance;
        }

        public void setMzTolerance(float mzTolerance) {
            this.mzTolerance = mzTolerance;
        }

        public int getNumHits() {
            return numHits;
        }

        public void setNumHits(int numHits) {
            this.numHits = numHits;
        }

        public int getScoreThreshold() {
            return scoreThreshold;
        }

        public void setScoreThreshold(int scoreThreshold) {
            this.scoreThreshold = scoreThreshold;
        }

        void fromUserParameters(UserParameters ups) {
            mzTolerance = ups.getSpectrumSearchMzTolerance();
            numHits = ups.getSpectrumSearchNumHits();
            scoreThreshold = Math.round(1000 * ups.getSpectrumSearchScoreThreshold());
        }

        void toUserParameters(UserParameters ups) {
            ups.setSpectrumSearchMzTolerance(mzTolerance);
            ups.setSpectrumSearchNumHits(numHits);
            ups.setSpectrumSearchScoreThreshold(scoreThreshold / 1000.0F);
        }
    }
}
