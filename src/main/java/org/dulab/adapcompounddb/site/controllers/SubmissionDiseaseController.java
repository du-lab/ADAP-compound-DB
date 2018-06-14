package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.SubmissionSource;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SubmissionDiseaseController {

    private static final String SESSION_ATTRIBUTE_REDIRECT_URL = "PRIOR_URL";

    private final SubmissionService submissionService;

    @Autowired
    public SubmissionDiseaseController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/diseases/", method = RequestMethod.GET)
    public String view(Model model) {

        List<ControllerUtils.CategoryWithSubmissionCount> diseases = submissionService.getAllDiseases()
                .stream()
                .map(s -> new ControllerUtils.CategoryWithSubmissionCount(
                        s,
                        submissionService.countByDiseaseId(s.getId())))
                .collect(Collectors.toList());

        model.addAttribute("sources", sources);
        return "submission/view_sources";
    }

    @RequestMapping(value = "/sources/add/", method = RequestMethod.GET)
    public String add(HttpSession session, Model model, @RequestHeader(value = "referer") String referer) {
        session.setAttribute(SESSION_ATTRIBUTE_REDIRECT_URL, referer);
        model.addAttribute("form", new Form());
        return "submission/edit_source";
    }

    @RequestMapping(value = "/sources/add", method = RequestMethod.POST)
    public String add(HttpSession session, Model model, @Valid Form form, Errors errors) {
        if (errors.hasErrors())
            return "submission/edit_source";

        return save(session, model, new SubmissionSource(), form);
    }

    @RequestMapping(value = "/sources/{id:\\d+}/", method = RequestMethod.GET)
    public String edit(@PathVariable("id") long id, HttpSession session, Model model,
                       @RequestHeader(value = "referer") String referer) {

        SubmissionSource source = submissionService
                .findSubmissionSource(id)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Submission Source with ID = %d is missing.", id)));

        session.setAttribute(SESSION_ATTRIBUTE_REDIRECT_URL, referer);

        Form form = new Form();
        form.setName(source.getName());
        form.setDescription(source.getDescription());
        model.addAttribute("form", form);

        return "submission/edit_source";
    }

    @RequestMapping(value = "/sources/{id:\\d+}/", method = RequestMethod.POST)
    public String edit(@PathVariable("id") long id, HttpSession session, Model model, @Valid Form form, Errors errors) {
        if (errors.hasErrors())
            return "submission/edit_source";

        SubmissionSource source = submissionService
                .findSubmissionSource(id)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Submission Source with ID = %d is missing.", id)));

        return save(session, model, source, form);
    }


    public String save(HttpSession session, Model model, SubmissionSource source, Form form) {

        source.setName(form.getName());
        source.setDescription(form.getDescription());

        try {
            submissionService.saveSubmissionSource(source);
        } catch (ConstraintViolationException e) {
            model.addAttribute("violationErrors", e.getConstraintViolations());
            return "submission/edit_source";
        }

        String redirectUrl = session.getAttribute(SESSION_ATTRIBUTE_REDIRECT_URL).toString();
        if (redirectUrl == null)
            redirectUrl = "/sources/";

        return "redirect:" + redirectUrl;
    }


    @RequestMapping(value = "/sources/{id:\\d+}/delete/", method = RequestMethod.GET)
    public String delete(@PathVariable("id") long id, @RequestHeader(value = "referer") String referer) {
        submissionService.deleteSubmissionSource(id);
        return "redirect:" + referer;
    }


//    public static class Form {
//
//        @NotBlank(message = "The field Name is required")
//        private String name;
//
//        private String description;
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getDescription() {
//            return description;
//        }
//
//        public void setDescription(String description) {
//            this.description = description;
//        }
//    }
//
//
//    public static class DiseaseWithSubmissionCount {
//
//        private final SubmissionSource source;
//        private final long count;
//
//        private SourceWithSubmissionCount(SubmissionSource source, long count) {
//            this.source = source;
//            this.count = count;
//        }
//
//        public SubmissionSource getSource() {
//            return source;
//        }
//
//        public long getCount() {
//            return count;
//        }
//    }
}
