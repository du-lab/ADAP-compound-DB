package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.validation.NotBlank;
import org.dulab.adapcompounddb.models.SampleSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Date;

@Controller
//@ControllerAdvice
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
        model.addAttribute("sampleSourceTypeList", SampleSourceType.values());
    }

    /********************************
    ***** View File / Submission *****
     ********************************/

    @RequestMapping(value = "/file/", method = RequestMethod.GET)
    public String fileView(HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        if (submission == null)
            return redirectFileUpload();

        return view(Submission.from(session), model);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/", method = RequestMethod.GET)
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
        form.setSampleSourceType(submission.getSampleSourceType());
        form.setSubmissionCategoryId(submission.getCategory() == null ? 0 : submission.getCategory().getId());
        model.addAttribute("submissionForm", form);

        return "file/view";
    }

    /************************************
    ***** File / Submission Raw View *****
     ************************************/

    @RequestMapping(value = "/file/fileview/", method = RequestMethod.GET)
    public String fileRawView(HttpSession session, HttpServletResponse response) throws IOException {
        Submission submission = Submission.from(session);

        if (submission == null)
            return redirectFileUpload();

        rawView(response, Submission.from(session));
        return null;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/fileview/", method = RequestMethod.GET)
    public String rawView(@PathVariable("submissionId") long id, HttpServletResponse response, Model model)
            throws IOException {

        Submission submission = submissionService.findSubmission(id);

        if (submission == null)
            return submissionNotFound(model, id);

        rawView(response, submission);
        return null;
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
            return redirectFileUpload();

        rawDownload(response, submission);
        return null;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/filedownload/", method = RequestMethod.GET)
    public String submissionRawDownload(@PathVariable("submissionId") long id, HttpServletResponse response, Model model)
            throws IOException {

        Submission submission = submissionService.findSubmission(id);
        if (submission == null)
            return submissionNotFound(model, id);

        rawDownload(response, submission);
        return null;
    }

    private void rawDownload(HttpServletResponse response, Submission submission) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + submission.getFilename() + "\"");
        response.getOutputStream().write(submission.getFile());
    }

    /**********************************
    ***** File / Submission Submit *****
     **********************************/

    @RequestMapping(value = "/file/", method = RequestMethod.POST)
    public String fileView(HttpSession session, Model model, @Valid SubmissionForm form, Errors errors) {

        if (errors.hasErrors()) {
            return "file/view";
        }

        Submission submission = Submission.from(session);
        if (submission == null)
            return redirectFileUpload();

        submission.setUser(UserPrincipal.from(session));

        String response = submit(submission, model, form);
        Submission.clear(session);
        return response;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/", method = RequestMethod.POST)
    public String submissionView(@PathVariable("submissionId") long submissionId, Model model,
                                 @Valid SubmissionForm form, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("form", form);
            return "file/view";
        }

        Submission submission = submissionService.findSubmission(submissionId);
        if (submission == null)
            return submissionNotFound(model, submissionId);

        return submit(submission, model, form);
    }

    private String submit(Submission submission, Model model, SubmissionForm form) {

        submission.setName(form.getName());
        submission.setDescription(form.getDescription());
        submission.setSampleSourceType(form.getSampleSourceType());
        submission.setDateTime(new Date());
        if (form.getSubmissionCategoryId() != 0)
            submission.setCategory(submissionService.getSubmissionCategory(form.getSubmissionCategoryId()));

        try {
            submissionService.saveSubmission(submission);
        }
        catch (ConstraintViolationException e) {
            model.addAttribute("validationErrors", e.getConstraintViolations());
            model.addAttribute("form", form);
            return "file/view";
        }

        model.addAttribute("message", "Mass spectra are submitted successfully.");
        return "redirect:/submission/" + submission.getId() + "/";
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/delete/")
    public String delete(@PathVariable("submissionId") long id) {
        submissionService.delete(id);
        return "redirect:/account/";
    }

    private String redirectFileUpload() {
        return "redirect:/file/upload/";
    }

    private String submissionNotFound(Model model, long submissionId) {
        model.addAttribute("errorMessage", "Cannot find submission ID = " + submissionId);
        return "/notfound/";
    }


    public static class SubmissionForm {

        @NotBlank(message = "The field Name is required.")
        private String name;

        @NotBlank(message = "The field Description is required.")
        private String description;

        @NotNull(message = "The field Sample Source Type is required.")
        private SampleSourceType sampleSourceType;

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

        public SampleSourceType getSampleSourceType() {
            return sampleSourceType;
        }

        public void setSampleSourceType(SampleSourceType sampleSourceType) {
            this.sampleSourceType = sampleSourceType;
        }

        public long getSubmissionCategoryId() {
            return submissionCategoryId;
        }

        public void setSubmissionCategoryId(long submissionCategoryId) {
            this.submissionCategoryId = submissionCategoryId;
        }
    }
}
