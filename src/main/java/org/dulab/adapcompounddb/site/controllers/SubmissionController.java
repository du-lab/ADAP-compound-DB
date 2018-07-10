package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("submissionCategoryTypes")
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
        model.addAttribute("submissionCategoryTypes", SubmissionCategoryType.values());
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
        form.setCategoryMap(submissionService.findAllCategories());
        form.setName(submission.getName());
        form.setDescription(submission.getDescription());
        if (submission.getCategories() != null)
            form.setSubmissionCategoryIds(submission
                    .getCategories()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(SubmissionCategory::getId)
                    .collect(Collectors.toList()));

        model.addAttribute("submissionForm", form);

        return "file/view";
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

        if (submission == null)
            return redirectFileUpload();

        rawView(response, submission.getFiles().get(fileIndex));
        return null;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/{fileIndex:\\d+}/view/", method = RequestMethod.GET)
    public String rawView(@PathVariable("submissionId") long id,
                          @PathVariable("fileIndex") int fileIndex,
                          HttpServletResponse response, Model model) throws IOException {

        Submission submission = submissionService.findSubmission(id);

        if (submission == null)
            return submissionNotFound(model, id);

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

        form.setCategoryMap(submissionService.findAllCategories());

        submission.setName(form.getName());
        submission.setDescription(form.getDescription());
        submission.setDateTime(new Date());

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
            model.addAttribute("validationErrors", e.getConstraintViolations());
            model.addAttribute("submissionForm", form);
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

        private List<Long> submissionCategoryIds;

        private Map<SubmissionCategoryType, List<SubmissionCategory>> categoryMap;

//        public SubmissionForm(List<SubmissionCategory> categories) {
//
//            this.categoryMap = Arrays.stream(SubmissionCategoryType.values())
//                    .collect(Collectors
//                            .toMap(t -> t, t -> new ArrayList<>()));
//
//            categories.forEach(
//                    category -> this.categoryMap
//                            .get(category.getCategoryType())
//                            .add(category));
//        }


        public void setCategoryMap(List<SubmissionCategory> categories) {

            this.categoryMap = Arrays.stream(SubmissionCategoryType.values())
                    .collect(Collectors
                            .toMap(t -> t, t -> new ArrayList<>()));

            categories.forEach(
                    category -> this.categoryMap
                            .get(category.getCategoryType())
                            .add(category));
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

        public List<Long> getSubmissionCategoryIds() {
            return submissionCategoryIds;
        }

        public void setSubmissionCategoryIds(List<Long> submissionCategoryIds) {
            this.submissionCategoryIds = submissionCategoryIds;
        }

//        public SortedMap<SubmissionCategoryType, List<SubmissionCategory>> getCategoryMap() {
//            return categoryMap;
//        }

        public SubmissionCategoryType[] getSubmissionCategoryTypes() {
            return SubmissionCategoryType.values();
        }

        public List<SubmissionCategory> getSubmissionCategories(SubmissionCategoryType type) {
            return categoryMap.get(type);
        }
    }
}
