package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.SubmissionSpecimen;
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
public class SubmissionSpecimenController {

    private static final String SESSION_ATTRIBUTE_REDIRECT_URL = "PRIOR_URL";

    private final SubmissionService submissionService;

    @Autowired
    public SubmissionSpecimenController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/species/", method = RequestMethod.GET)
    public String view(Model model) {

        List<SpecimenWithSubmissionCount> species = submissionService.getAllSpecies()
                .stream()
                .map(s -> new SpecimenWithSubmissionCount(
                        s,
                        submissionService.countBySpecimenId(s.getId())))
                .collect(Collectors.toList());

        model.addAttribute("species", species);
        return "submission/view_species";
    }

    @RequestMapping(value = "/species/add/", method = RequestMethod.GET)
    public String add(HttpSession session, Model model, @RequestHeader(value = "referer") String referer) {
        session.setAttribute(SESSION_ATTRIBUTE_REDIRECT_URL, referer);
        model.addAttribute("form", new Form());
        return "submission/edit_specimen";
    }

    @RequestMapping(value = "/species/add", method = RequestMethod.POST)
    public String add(HttpSession session, Model model, @Valid Form form, Errors errors) {
        if (errors.hasErrors())
            return "submission/edit_specimen";

        return save(session, model, new SubmissionSpecimen(), form);
    }

    @RequestMapping(value = "/species/{id:\\d+}/", method = RequestMethod.GET)
    public String edit(@PathVariable("id") long id, HttpSession session, Model model,
                       @RequestHeader(value = "referer") String referer) {

        SubmissionSpecimen specimen = submissionService
                .findSubmissionSpecimen(id)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Submission Specimen with ID = %d is missing.", id)));

        session.setAttribute(SESSION_ATTRIBUTE_REDIRECT_URL, referer);

        Form form = new Form();
        form.setName(specimen.getName());
        form.setDescription(specimen.getDescription());
        model.addAttribute("form", form);

        return "submission/edit_specimen";
    }

    @RequestMapping(value = "/species/{id:\\d+}/", method = RequestMethod.POST)
    public String edit(@PathVariable("id") long id, HttpSession session, Model model, @Valid Form form, Errors errors) {
        if (errors.hasErrors())
            return "submission/edit_specimen";

        SubmissionSpecimen specimen = submissionService
                .findSubmissionSpecimen(id)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Submission Specimen with ID = %d is missing.", id)));

        return save(session, model, specimen, form);
    }


    public String save(HttpSession session, Model model, SubmissionSpecimen specimen, Form form) {

        specimen.setName(form.getName());
        specimen.setDescription(form.getDescription());

        try {
            submissionService.saveSubmissionSpecimen(specimen);
        } catch (ConstraintViolationException e) {
            model.addAttribute("violationErrors", e.getConstraintViolations());
            return "submission/edit_specimen";
        }

        String redirectUrl = session.getAttribute(SESSION_ATTRIBUTE_REDIRECT_URL).toString();
        if (redirectUrl == null)
            redirectUrl = "/species/";

        return "redirect:" + redirectUrl;
    }


    @RequestMapping(value = "/species/{id:\\d+}/delete/", method = RequestMethod.GET)
    public String delete(@PathVariable("id") long id, @RequestHeader(value = "referer") String referer) {
        submissionService.deleteSubmissionSpecimen(id);
        return "redirect:" + referer;
    }


    public static class Form {

        @NotBlank(message = "The field Name is required")
        private String name;

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


    public static class SpecimenWithSubmissionCount {

        private final SubmissionSpecimen specimen;
        private final long count;

        private SpecimenWithSubmissionCount(SubmissionSpecimen specimen, long count) {
            this.specimen = specimen;
            this.count = count;
        }

        public SubmissionSpecimen getSpecimen() {
            return specimen;
        }

        public long getCount() {
            return count;
        }
    }
}
