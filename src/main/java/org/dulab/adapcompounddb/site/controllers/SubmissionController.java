package org.dulab.adapcompounddb.site.controllers;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.dto.SubmissionDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"submissionCategoryTypes", "availableTags", "availableCategories"})
public class SubmissionController extends BaseController {

    private final SubmissionService submissionService;

    @Autowired
    public SubmissionController(SubmissionService submissionService,
                                SpectrumService spectrumService) {

        this.submissionService = submissionService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("submissionCategoryTypes", SubmissionCategoryType.values());
        model.addAttribute("availableTags", submissionService.findAllTags());

        Map<SubmissionCategoryType, List<SubmissionCategory>> availableCategories = Arrays.stream(SubmissionCategoryType.values())
                .collect(Collectors
                        .toMap(t -> t, t -> new ArrayList<>()));

        submissionService.findAllCategories()
                .forEach(category -> availableCategories
                        .get(category.getCategoryType())
                        .add(category));

        model.addAttribute("availableCategories", availableCategories);
    }

    /********************************
     ***** View File / Submission *****
     ********************************/

    @RequestMapping(value = "/file/", method = RequestMethod.GET)
    public String fileView(HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        if (submission == null) {
            return redirectFileUpload();
        }
        return edit(Submission.from(session), model);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/edit", method = RequestMethod.GET)
    public String editSubmission(@PathVariable("submissionId") long submissionId, Model model) {

        SubmissionDTO submission = submissionService.findSubmissionById(submissionId);

        if (submission == null) {
            return submissionNotFound(model, submissionId);
        }

        return view(submission, model, true);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/", method = RequestMethod.GET)
    public String viewSubmission(@PathVariable("submissionId") long submissionId, Model model) {

        SubmissionDTO submissionDTO = submissionService.findSubmissionById(submissionId);

        if (submissionDTO == null) {
            return submissionNotFound(model, submissionId);
        }

        return view(submissionDTO, model, false);
    }

    private String view(SubmissionDTO submissionDTO, Model model, boolean edit) {

        // User is authorized to edit the submission
        boolean authorized = submissionDTO.isAuthorized(getCurrentUserPrincipal());
        if (!authorized && edit) {
            return "redirect:/error?errorMsg=" + ACCESS_DENIED_MESSAGE;
        }

        model.addAttribute("submissionDTO", submissionDTO);
        model.addAttribute("authorized", authorized);
        model.addAttribute("edit", edit);

        return "submission/view";
    }

    private String edit(Submission submission, Model model) {

        SubmissionDTO submissionDTO = submissionService.convertToDTO(null, submission);
        model.addAttribute("submissionDTO", submissionDTO);
        model.addAttribute("authenticated", isAuthenticated());  // User is logged in

        return "file/view";
    }

    private SubmissionForm createSubmissionForm(Submission submission) {
        SubmissionForm form = new SubmissionForm();
//        form.setCategoryMap(submissionService.findAllCategories());

        form.setName(submission.getName());
        form.setDescription(submission.getDescription());
        form.setReference(submission.getReference());

        if (submission.getTags() != null) {
            form.setTags(submission
                    .getTags()
                    .stream()
                    .map(SubmissionTag::getId)
                    .map(SubmissionTagId::getName)
                    .collect(Collectors.joining(",")));
        }

        if (submission.getCategories() != null) {
            form.setSubmissionCategoryIds(submission
                    .getCategories()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(SubmissionCategory::getId)
                    .collect(Collectors.toList()));
        }
        return form;
    }

    /**********************
     ***** File Clear *****
     **********************/
    @RequestMapping(value = "/file/clear/", method = RequestMethod.GET)
    public String clear(HttpSession session) {
        Submission.clear(session);
        return "redirect:/file/upload/";
    }

    /************************************
     ***** File / Submission Raw View *****
     ************************************/
    @RequestMapping(value = "/file/{fileIndex:\\d+}/view/", method = RequestMethod.GET)
    public String fileRawView(@PathVariable("fileIndex") int fileIndex,
                              HttpSession session, HttpServletResponse response) throws IOException {

        Submission submission = Submission.from(session);

        if (submission == null) {
            return redirectFileUpload();
        }

        rawView(response, submission.getFiles().get(fileIndex));
        return null;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/{fileIndex:\\d+}/view/", method = RequestMethod.GET)
    public String rawView(@PathVariable("submissionId") long id,
                          @PathVariable("fileIndex") int fileIndex,
                          HttpServletResponse response, Model model) throws IOException {

        Submission submission = submissionService.findSubmission(id);

        if (submission == null) {
            return submissionNotFound(model, id);
        }

        rawView(response, submission.getFiles().get(fileIndex));
        return null;
    }

    private void rawView(HttpServletResponse response, File file) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        response.getOutputStream().write(file.getContent());
    }

    /****************************************
     ***** File / Submission Raw Download *****
     ****************************************/

    @RequestMapping(value = "/file/{fileIndex:\\d+}/download/", method = RequestMethod.GET)
    public String fileRawDownload(@PathVariable("fileIndex") int fileIndex,
                                  HttpSession session,
                                  HttpServletResponse response) throws IOException {

        Submission submission = Submission.from(session);
        if (submission == null)
            return redirectFileUpload();

        rawDownload(response, submission.getFiles().get(fileIndex));
        return null;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/{fileIndex:\\d+}/download/", method = RequestMethod.GET)
    public String submissionRawDownload(@PathVariable("submissionId") long id,
                                        @PathVariable("fileIndex") int fileIndex,
                                        HttpServletResponse response, Model model)
            throws IOException {

        Submission submission = submissionService.findSubmission(id);
        if (submission == null)
            return submissionNotFound(model, id);

        rawDownload(response, submission.getFiles().get(fileIndex));
        return null;
    }

    private void rawDownload(HttpServletResponse response, File file) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.getOutputStream().write(file.getContent());
    }

    /**********************************
     ***** File / Submission Submit *****
     **********************************/
    @RequestMapping(value = "/file/submit", method = RequestMethod.POST)
    public String fileView(HttpSession session, Model model, @Valid SubmissionDTO submissionDTO, Errors errors) {

        Submission submission = Submission.from(session);
        if (errors.hasErrors()) {
            model.addAttribute("authenticated", isAuthenticated());
            model.addAttribute("submissionDTO", submissionDTO);
            return "file/view";
        }

        if (submission == null)
            return redirectFileUpload();

        submission.setUser(getCurrentUserPrincipal());

        String response = submit(submission, model, submissionDTO);
        Submission.clear(session);
        return response;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/edit", method = RequestMethod.POST)
    public String submissionView(@PathVariable("submissionId") long submissionId, Model model, HttpSession session,
                                 @Valid SubmissionDTO submissionDTO, Errors errors) {

        Submission submission = submissionService.findSubmission(submissionId);
        if (errors.hasErrors()) {
            submissionDTO.setFiles(submission.getFiles());
            model.addAttribute("submissionDTO", submissionDTO);
            model.addAttribute("edit", true);
            return "submission/view";
        }

        if (submission == null) {
            return submissionNotFound(model, submissionId);
        }

        return submit(submission, model, submissionDTO);
    }

    private String submit(Submission submission, Model model, SubmissionDTO form) {

        submission.setName(form.getName());
        submission.setDescription(form.getDescription());
        submission.setReference(form.getReference());
        submission.setDateTime(new Date());

        List<SubmissionTag> tags = new ArrayList<>();
        for (String name : form.getTags().split(",")) {

            if (name.trim().isEmpty())
                continue;

            SubmissionTag submissionTag = new SubmissionTag();
            submissionTag.setId(new SubmissionTagId(submission, name.toLowerCase()));
            tags.add(submissionTag);
        }
        submission.setTags(tags);

        List<SubmissionCategory> categories = new ArrayList<>();
        for (long id : form.getSubmissionCategoryIds())
            if (id > 0)
                categories.add(submissionService
                        .findSubmissionCategory(id)
                        .orElseThrow(() -> new IllegalStateException(
                                String.format("Submission Category with ID = %d cannot be found.", id))));
        submission.setCategories(categories);

        try {
            submissionService.saveSubmission(submission);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            model.addAttribute("validationErrors", e.getConstraintViolations());
            model.addAttribute("submissionForm", form);
            return "file/view";
        } catch (Exception e) {
            // TODO: handle exception
            throw e;
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

        @URL(message = "The field Reference must be a valid URL.")
        private String reference;

        private String tags;

        private List<Long> submissionCategoryIds;

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

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public List<Long> getSubmissionCategoryIds() {
            return submissionCategoryIds;
        }

        public void setSubmissionCategoryIds(List<Long> submissionCategoryIds) {
            this.submissionCategoryIds = submissionCategoryIds;
        }
    }
}
