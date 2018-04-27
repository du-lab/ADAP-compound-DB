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

    /********************************
    ***** View File / Submission *****
     ********************************/

    @RequestMapping(value = "/file/view/", method = RequestMethod.GET)
    public String fileView(HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        if (submission == null)
            return fileUpload();

        return view(Submission.from(session), model);
    }

    @RequestMapping(value = "{submissionId:\\d+}/", method = RequestMethod.GET)
    public String viewSubmission(@PathVariable("submissionId") long submissionId, Model model) {

        Submission submission = submissionService.findSubmission(submissionId);

        if (submission == null)
            return submissionNotFound(model, submissionId);

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

    /************************************
    ***** File / Submission Raw View *****
     ************************************/

    @RequestMapping(value = "/file/rawview/", method = RequestMethod.GET)
    public String fileRawView(HttpSession session, HttpServletResponse response) throws IOException {
        Submission submission = Submission.from(session);

        if (submission == null)
            return fileUpload();

        rawView(response, Submission.from(session));
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/rawview/", method = RequestMethod.GET)
    public String rawView(@PathVariable("submissionId") long id, HttpServletResponse response, Model model)
            throws IOException {

        Submission submission = submissionService.findSubmission(id);

        if (submission == null)
            return submissionNotFound(model, id);

        rawView(response, submission);
    }

    private void rawView(HttpServletResponse response, Submission submission) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "inline; filename=\"" + submission.getFilename() + "\"");
        response.getOutputStream().write(submission.getFile());
    }

    /****************************************
    ***** File / Submission Raw Download *****
     ****************************************/

    @RequestMapping(value = "/file/filedownload/", method = RequestMethod.GET)
    public String fileRawDownload(HttpSession session, HttpServletResponse response, Model model)
            throws IOException {

        Submission submission = Submission.from(session);
        if (submission == null)
            return fileUpload();

        rawDownload(response, submission);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/filedownload/", method = RequestMethod.GET)
    public String submissionRawDownload(@PathVariable("submissionId") long id, HttpServletResponse response, Model model)
            throws IOException {

        Submission submission = submissionService.findSubmission(id);
        if (submission == null)
            return submissionNotFound(model, id);

        rawDownload(response, submission);
    }

    private void rawDownload(HttpServletResponse response, Submission submission) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + submission.getFilename() + "\"");
        response.getOutputStream().write(submission.getFile());
    }

    /**********************************
    ***** File / Submission Submit *****
     **********************************/

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

    private String fileUpload() {
        return "redirect:/file/upload/";
    }

    private String submissionNotFound(Model model, long submissionId) {
        model.addAttribute("errorMessage", "Cannot find submission ID = " + submissionId);
        return "/notfound/";
    }

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
