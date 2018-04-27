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
import java.io.IOException;
import java.util.Date;

@Controller
@ControllerAdvice
//@RequestMapping("submission/")
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

    @RequestMapping(value = "/file/view/", method = RequestMethod.GET)
    public String fileView(HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        if (submission == null)
            return "redirect:/file/upload/";

        return view(Submission.from(session), model);
    }

    @RequestMapping(value = "{submissionId:\\d+}/", method = RequestMethod.GET)
    public String viewSubmission(@PathVariable("submissionId") long submissionId, Model model) {

        Submission submission = submissionService.findSubmission(submissionId);

        if (submission == null) {
            model.addAttribute("errorMessage", "Cannot find submission ID = " + submissionId);
            return "redirect:/notfound/";
        }

        return view(submission, model);
    }

    private String view(Submission submission, Model model) {

        model.addAttribute("submission", submission);

        SubmissionForm form = new SubmissionForm();
        form.setName(submission.getName());
        form.setDescription(submission.getDescription());
        form.setSubmissionCategoryId(submission.getCategory() == null ? 0 : submission.getCategory().getId());
        model.addAttribute("form", form);

        return "file/view";
    }

    @RequestMapping(value = "{submissionId:\\d+}/", method = RequestMethod.POST)
    public ModelAndView submit(HttpSession session, Model model,
                               @PathVariable("submissionId") long submissionId,
                               @Valid SubmissionController.SubmissionForm form, Errors errors) {

        if (errors.hasErrors())
            return new ModelAndView("file/view");

        Submission submission = getSubmission(submissionId, session);
        submission.setName(form.getName());
        submission.setDescription(form.getDescription());
        submission.setDateTime(new Date());
        submission.setUser(UserPrincipal.from(session));
        submission.setCategory(submissionService
                .getSubmissionCategory(form.getSubmissionCategoryId()));

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

        Submission submission = submissionService.findSubmission(id);

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


//    @RequestMapping(value = "{submissionId:\\d+}/{spectrumId:\\d+}/match/", method = RequestMethod.GET)
//    public String matchGet(@PathVariable("submissionId") long submissionId,
//                        @PathVariable("spectrumId") int spectrumId,
//                        HttpSession session,
//                        Model model) {
//
//        Submission submission = getSubmission(submissionId, session);
//        Spectrum querySpectrum = submission.getSpectra().get(spectrumId);
//
//        UserParameter userParameters = new UserParameter();
//
//        SpectrumSearchForm form = new SpectrumSearchForm();
//        form.fromUserParameters(userParameters);
//        form.setChromatographyTypeCheck(false);
//        form.setSubmissionCategoryCheck(false);
//
//        model.addAttribute("querySpectrum", querySpectrum);
//        model.addAttribute("form", form);
//        model.addAttribute("chromatographyTypes", ChromatographyType.values());
//
//        return "file/match";
//    }
//
//    @RequestMapping(value = "{submissionId:\\d+}/{spectrumId:\\d+}/match/", method = RequestMethod.POST)
//    public String matchPost(@PathVariable("submissionId") long submissionId,
//                            @PathVariable("spectrumId") int spectrumId,
//                            HttpSession session, Model model,
//                            @Valid SpectrumSearchForm form, Errors errors) {
//
//        Submission submission = getSubmission(submissionId, session);
//        Spectrum querySpectrum = submission.getSpectra().get(spectrumId);
//
//        UserParameter userParameters = new UserParameter();
//        form.toUserParameters(userParameters);
//
//        CriteriaBlock criteria = new CriteriaBlock(SetOperator.AND);
//        if (form.isChromatographyTypeCheck())
//            criteria.add(
//                    new Criterion("ChromatographyType", ComparisonOperator.EQ, form.chromatographyType));
//        if (form.isSubmissionCategoryCheck()) {
//            CriteriaBlock categories = new CriteriaBlock(SetOperator.OR);
//            for (long id : form.getSubmissionCategoryIds())
//                categories.add(
//                        new Criterion("SubmissionCategoryId", ComparisonOperator.EQ, id));
//            criteria.add(new Criterion("", ComparisonOperator.BLOCK, categories));
//        }
//
//        try {
//            List<Hit> hits = spectrumService.match(querySpectrum, criteria, userParameters);
//            model.addAttribute("hits", hits);
//        }
//        catch (EmptySearchResultException e) {
//            model.addAttribute("searchResultMessage", e.getMessage());
//        }
//
//        model.addAttribute("querySpectrum", querySpectrum);
//        model.addAttribute("form", form);
//
//        return "file/match";
//    }

    private Submission getSubmission(long id, HttpSession session) {
        try {
            return submissionService.findSubmission(id);
        }
        catch (EmptySearchResultException e) {
            return Submission.from(session);
        }
    }


    public static class SubmissionForm {

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
}
