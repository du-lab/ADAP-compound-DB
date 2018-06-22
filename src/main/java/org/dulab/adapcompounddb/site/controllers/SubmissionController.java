package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.*;
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
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
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

        SubmissionForm form = new SubmissionForm(
                submissionService.getAllSources(),
                submissionService.getAllSpecies(),
                submissionService.getAllDiseases());

        form.setName(submission.getName());
        form.setDescription(submission.getDescription());
        if (submission.getSource() != null)
            form.setSubmissionSourceId(submission.getSource().getId());
        if (submission.getSpecimen() != null)
            form.setSubmissionSpecimenId(submission.getSpecimen().getId());
        if (submission.getDisease() != null)
            form.setSubmissionDiseaseId(submission.getDisease().getId());
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
    public String submissionView(@PathVariable("submissionId") long submissionId, Model model, HttpSession session,
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
        submission.setDateTime(new Date());

        submission.setSource(
                form.getSubmissionSourceId() == 0 ? null :
                        submissionService.findSubmissionSource(form.getSubmissionSourceId())
                                .orElseThrow(() -> new IllegalStateException(String.format(
                                        "Submission Source with ID = %d cannot be found.",
                                        form.getSubmissionSourceId()))));


        submission.setSpecimen(
                form.getSubmissionSpecimenId() == 0 ? null :
                        submissionService.findSubmissionSpecimen(form.getSubmissionSpecimenId())
                                .orElseThrow(() -> new IllegalStateException(String.format(
                                        "Submission Specimen with ID = %d cannot be found.",
                                        form.getSubmissionSpecimenId()))));

        submission.setDisease(
                form.getSubmissionDiseaseId() == 0 ? null :
                        submissionService.findSubmissionDisease(form.getSubmissionDiseaseId())
                                .orElseThrow(() -> new IllegalStateException(String.format(
                                        "Submission Disease with ID = %d cannot be found.",
                                        form.getSubmissionDiseaseId()))));

        try {
            submissionService.saveSubmission(submission);
        } catch (ConstraintViolationException e) {
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

        private String description;

        private Long submissionSourceId;

        private Long submissionSpecimenId;

        private Long submissionDiseaseId;

        private final List<SubmissionSource> sources;
        private final List<SubmissionSpecimen> species;
        private final List<SubmissionDisease> diseases;

        public SubmissionForm(List<SubmissionSource> sources,
                              List<SubmissionSpecimen> species,
                              List<SubmissionDisease> diseases) {

            this.sources = sources;
            this.species = species;
            this.diseases = diseases;
        }

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

        public Long getSubmissionSourceId() {
            return submissionSourceId;
        }

        public void setSubmissionSourceId(Long submissionSourceId) {
            this.submissionSourceId = submissionSourceId;
        }

        public Long getSubmissionSpecimenId() {
            return submissionSpecimenId;
        }

        public void setSubmissionSpecimenId(Long submissionSpecimenId) {
            this.submissionSpecimenId = submissionSpecimenId;
        }

        public Long getSubmissionDiseaseId() {
            return submissionDiseaseId;
        }

        public void setSubmissionDiseaseId(Long submissionDiseaseId) {
            this.submissionDiseaseId = submissionDiseaseId;
        }

        public List<SubmissionSource> getSources() {
            return sources;
        }

        public List<SubmissionSpecimen> getSpecies() {
            return species;
        }

        public List<SubmissionDisease> getDiseases() {
            return diseases;
        }
    }
}
