package org.dulab.adapcompounddb.site.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.hibernate.validator.constraints.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@SessionAttributes({"availableTags"})
public class SubmissionController extends BaseController {

    private static final Logger LOGGER = LogManager.getLogger(SubmissionController.class);

    private static final String SESSION_ATTRIBUTE_KEY = "currentUser";
    private final SubmissionService submissionService;

    @Autowired
    public SubmissionController(final SubmissionService submissionService, final SpectrumService spectrumService) {

        this.submissionService = submissionService;
    }

    @ModelAttribute
    public void addAttributes(final Model model) {
        model.addAttribute("availableTags", submissionService.findUniqueTagStrings());
        /*model.addAttribute("submissionCategoryTypes", SubmissionCategoryType.values());

        final Map<SubmissionCategoryType, List<SubmissionCategory>> availableCategories = Arrays
                .stream(SubmissionCategoryType.values()).collect(Collectors.toMap(t -> t, t -> new ArrayList<>()));

        submissionService.findAllCategories()
                .forEach(category -> availableCategories.get(category.getCategoryType()).add(category));

        model.addAttribute("availableCategories", availableCategories);*/
    }

    /********************************
     ***** View File / Submission *****
     ********************************/

    @RequestMapping(value = "/file/", method = RequestMethod.GET)
    public String fileView(final HttpSession session, final Model model) {
        final Submission submission = Submission.from(session);
        if (submission == null) {
            return redirectFileUpload();
        }
        final boolean authenticated = session.getAttribute(SESSION_ATTRIBUTE_KEY) != null;
        return edit(Submission.from(session), model, authenticated);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/edit", method = RequestMethod.GET)
    public String editSubmission(@PathVariable("submissionId") final long submissionId, final Model model) {

        final Submission submission = submissionService.findSubmission(submissionId);

        if (submission == null) {
            return submissionNotFound(model, submissionId);
        }

        return view(submission, model, true);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/", method = RequestMethod.GET)
    public String viewSubmission(@PathVariable("submissionId") final long submissionId, final Model model) {

        final Submission submission = submissionService.findSubmission(submissionId);

        if (submission == null) {
            return submissionNotFound(model, submissionId);
        }

        return view(submission, model, false);
    }

    private String view(final Submission submission, final Model model, final boolean edit) {

        // User is authorized to edit the submission
        final boolean authorized = submission.isAuthorized(getCurrentUserPrincipal());
        if (!authorized && edit) {
            return "redirect:/error?errorMsg=" + ACCESS_DENIED_MESSAGE;
        }
        final SubmissionForm submissionForm = createSubmissionForm(submission);
        submissionForm.setAuthorized(authorized);

        model.addAttribute("submission", submission);
        model.addAttribute("submissionForm", submissionForm);
        model.addAttribute("edit", edit);

        return "submission/view";
    }

    private String edit(final Submission submission, final Model model, final boolean authenticated) {

        final SubmissionForm submissionForm = createSubmissionForm(submission);
        model.addAttribute("submission", submission);
        model.addAttribute("submissionForm", submissionForm);
        model.addAttribute("authenticated", authenticated); // User is logged in

        if (authenticated) {
            return "submission/view";
        } else {
            return "file/view";
        }
    }

    private SubmissionForm createSubmissionForm(final Submission submission) {
        final SubmissionForm form = new SubmissionForm();
//        form.setCategoryMap(submissionService.findAllCategories());
        form.setId(submission.getId());
        form.setExternalId(submission.getExternalId());
        form.setName(submission.getName());
        form.setDescription(submission.getDescription());
        form.setReference(submission.getReference());

        if (submission.getTags() != null) {
            //format tag into the same format created by tagify which is JsonArray
            JSONArray jsonArray = new JSONArray();
            for (SubmissionTag submissionTag : submission.getTags()) {
                jsonArray.put(submissionTag.toString());
            }
            form.setTags(jsonArray.toString());
        }

        if (submission.getCategories() != null) {
            form.setSubmissionCategoryIds(submission.getCategories().stream().filter(Objects::nonNull)
                    .map(SubmissionCategory::getId).collect(Collectors.toList()));
        }
        return form;
    }

    /**********************
     ***** File Clear *****
     **********************/
    @RequestMapping(value = "/file/clear/", method = RequestMethod.GET)
    public String clear(final HttpSession session) {
        Submission.clear(session);
        session.removeAttribute("group_search_results");
        return "redirect:/file/upload/";
    }

    /************************************
     ***** File / Submission Raw View *****
     ************************************/
    @RequestMapping(value = "/file/{fileIndex:\\d+}/view/", method = RequestMethod.GET)
    public String fileRawView(@PathVariable("fileIndex") final int fileIndex, final HttpSession session,
                              final HttpServletResponse response) throws IOException {

        final Submission submission = Submission.from(session);

        if (submission == null) {
            return redirectFileUpload();
        }

        rawView(response, submission.getFiles().get(fileIndex));
        return null;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/{fileIndex:\\d+}/view/", method = RequestMethod.GET)
    public String rawView(@PathVariable("submissionId") final long id, @PathVariable("fileIndex") final int fileIndex,
                          final HttpServletResponse response, final Model model) throws IOException {

        final Submission submission = submissionService.findSubmission(id);

        if (submission == null) {
            return submissionNotFound(model, id);
        }

        rawView(response, submission.getFiles().get(fileIndex));
        return null;
    }

    private void rawView(final HttpServletResponse response, final File file) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        response.getOutputStream().write(file.getContent());
    }

    /****************************************
     ***** File / Submission Raw Download *****
     ****************************************/

    @RequestMapping(value = "/file/{fileIndex:\\d+}/download/", method = RequestMethod.GET)
    public String fileRawDownload(@PathVariable("fileIndex") final int fileIndex, final HttpSession session,
                                  final HttpServletResponse response) throws IOException {

        final Submission submission = Submission.from(session);
        if (submission == null) {
            return redirectFileUpload();
        }

        rawDownload(response, submission.getFiles().get(fileIndex));
        return null;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/{fileIndex:\\d+}/download/", method = RequestMethod.GET)
    public String submissionRawDownload(@PathVariable("submissionId") final long id,
                                        @PathVariable("fileIndex") final int fileIndex, final HttpServletResponse response,
                                        final Model model)
            throws IOException {

        final Submission submission = submissionService.findSubmission(id);
        if (submission == null) {
            return submissionNotFound(model, id);
        }

        rawDownload(response, submission.getFiles().get(fileIndex));
        return null;
    }

    private void rawDownload(final HttpServletResponse response, final File file) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.getOutputStream().write(file.getContent());
    }

    /**********************************
     ***** File / Submission Submit *****
     **********************************/
    @RequestMapping(value = "/file/submit", method = RequestMethod.POST)
    public String fileView(final HttpSession session, final Model model, @Valid final SubmissionForm submissionForm,
                           final Errors errors) {

        final Submission submission = Submission.from(session);
        if (errors.hasErrors()) {
            model.addAttribute("authenticated", isAuthenticated());
            model.addAttribute("submissionForm", submissionForm);
            return "submission/view";
        }

        if (submission == null) {
            return redirectFileUpload();
        }

        submission.setUser(getCurrentUserPrincipal());

        final String response = submit(submission, model, submissionForm);
        Submission.clear(session);
        return response;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/edit", method = RequestMethod.POST)
    public String submissionView(@PathVariable("submissionId") final long submissionId, final Model model,
                                 final HttpSession session, @Valid final SubmissionForm submissionForm, final Errors errors) {

        final Submission submission = submissionService.findSubmission(submissionId);
        if (errors.hasErrors()) {
            model.addAttribute("submissionForm", submissionForm);
            model.addAttribute("submission", submission);
            model.addAttribute("edit", true);
            return "submission/view";
        }

        if (submission == null) {
            return submissionNotFound(model, submissionId);
        }

        return submit(submission, model, submissionForm);
    }

    private String submit(final Submission submission, final Model model, final SubmissionForm submissionForm) {

        submission.setName(submissionForm.getName());
        submission.setExternalId(submissionForm.getExternalId());
        submission.setDescription(submissionForm.getDescription());
        submission.setReference(submissionForm.getReference());
        submission.setDateTime(new Date());

        List<SubmissionTag> tags = submission.getTags();
        if (tags == null) {
            tags = new ArrayList<>();

            submission.setTags(tags);
        }
        tags.clear();

        String tagString = submissionForm.getTags();
        if (tagString != null && tagString.length() > 0) {

            JSONArray tagArray = new JSONArray(tagString);

            for (int i = 0; i < tagArray.length(); i++) {
                JSONObject tagJsonObject = tagArray.getJSONObject(i);
                String name = tagJsonObject.getString("value");
                if (name.trim().isEmpty()) {
                    continue;
                }
                tags.add(new SubmissionTag(submission, name));
            }
        }

        //        final List<SubmissionCategory> categories = new ArrayList<>();
        //        for (final long id : submissionForm.getSubmissionCategoryIds()) {
        //            if (id > 0) {
        //                categories.add(submissionService.findSubmissionCategory(id).orElseThrow(() -> new IllegalStateException(
        //                        String.format("Submission Category with ID = %d cannot be found.", id))));
        //            }
        //        }
        //submission.setCategories(categories);

        try {
            final long time = System.currentTimeMillis();
            submissionService.saveSubmission(submission);
            LOGGER.info(String.format(
                    "New submission is saved to the database in %.3f sec.",
                    (System.currentTimeMillis() - time) / 1000.0));
        } catch (final ConstraintViolationException e) {
            e.printStackTrace();
            model.addAttribute("validationErrors", e.getConstraintViolations());
            model.addAttribute("submissionForm", submissionForm);
            return "file/view";
        } catch (final Exception e) {
            throw e;
        }

        model.addAttribute("message", "Mass spectra are submitted successfully.");
        return "redirect:/submission/" + submission.getId() + "/";
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/delete")
    public String delete(@PathVariable("submissionId") final long id, @RequestHeader(value = "referer",
            required = false) final String referer) {
        submissionService.delete(id);
//        String newReferer;
//        if (referer.contains("?")) {
//            newReferer = referer.split("\\?")[0];
//        } else {
//            newReferer = referer;
//        }
        return "redirect:/account/";
    }

    private String redirectFileUpload() {
        return "redirect:/file/upload/";
    }

    private String submissionNotFound(final Model model, final long submissionId) {
        model.addAttribute("errorMessage", "Cannot find submission ID = " + submissionId);
        return "/notfound/";
    }

    public static class SubmissionForm {

        private Long id;

        @NotBlank(message = "The field Name is required.")
        private String name;

        private String externalId;

        private String description;

        @URL(message = "The field Reference must be a valid URL.")
        private String reference;

        private String tags;

        private List<Long> submissionCategoryIds;

        private boolean authorized;

        public Long getId() {
            return id;
        }

        public boolean isAuthorized() {
            return authorized;
        }

        public void setAuthorized(final boolean authorized) {
            this.authorized = authorized;
        }

        public void setId(final Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getExternalId() {
            return externalId;
        }

        public void setExternalId(String externalId) {
            this.externalId = externalId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(final String description) {
            this.description = description;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(final String reference) {
            this.reference = reference;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(final String tags) {
            this.tags = tags;
        }

        public List<Long> getSubmissionCategoryIds() {
            return submissionCategoryIds;
        }

        public void setSubmissionCategoryIds(final List<Long> submissionCategoryIds) {
            this.submissionCategoryIds = submissionCategoryIds;
        }
    }
}
