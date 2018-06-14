package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.SubmissionDisease;
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

        model.addAttribute("diseases", diseases);
        return "submission/view_diseases";
    }

    @RequestMapping(value = "/diseases/add/", method = RequestMethod.GET)
    public String add(HttpSession session, Model model, @RequestHeader(value = "referer") String referer) {
        session.setAttribute(SESSION_ATTRIBUTE_REDIRECT_URL, referer);
        model.addAttribute("categoryForm", new ControllerUtils.CategoryForm());
        return "submission/edit_disease";
    }

    @RequestMapping(value = "/diseases/add", method = RequestMethod.POST)
    public String add(HttpSession session, Model model, @Valid ControllerUtils.CategoryForm form, Errors errors) {
        if (errors.hasErrors())
            return "submission/edit_disease";

        return save(session, model, new SubmissionDisease(), form);
    }

    @RequestMapping(value = "/diseases/{id:\\d+}/", method = RequestMethod.GET)
    public String edit(@PathVariable("id") long id, HttpSession session, Model model,
                       @RequestHeader(value = "referer") String referer) {

        SubmissionDisease disease = submissionService
                .findSubmissionDisease(id)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Submission Disease with ID = %d is missing.", id)));

        session.setAttribute(SESSION_ATTRIBUTE_REDIRECT_URL, referer);

        ControllerUtils.CategoryForm form = new ControllerUtils.CategoryForm();
        form.setName(disease.getName());
        form.setDescription(disease.getDescription());
        model.addAttribute("categoryForm", form);

        return "submission/edit_disease";
    }

    @RequestMapping(value = "/diseases/{id:\\d+}/", method = RequestMethod.POST)
    public String edit(@PathVariable("id") long id, HttpSession session, Model model,
                       @Valid ControllerUtils.CategoryForm form, Errors errors) {

        if (errors.hasErrors())
            return "submission/edit_disease";

        SubmissionDisease disease = submissionService
                .findSubmissionDisease(id)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Submission Disease with ID = %d is missing.", id)));

        return save(session, model, disease, form);
    }


    public String save(HttpSession session, Model model, SubmissionDisease disease, ControllerUtils.CategoryForm form) {

        disease.setName(form.getName());
        disease.setDescription(form.getDescription());

        try {
            submissionService.saveSubmissionDisease(disease);
        } catch (ConstraintViolationException e) {
            model.addAttribute("violationErrors", e.getConstraintViolations());
            return "submission/edit_disease";
        }

        String redirectUrl = session.getAttribute(SESSION_ATTRIBUTE_REDIRECT_URL).toString();
        if (redirectUrl == null)
            redirectUrl = "/diseases/";

        return "redirect:" + redirectUrl;
    }


    @RequestMapping(value = "/diseases/{id:\\d+}/delete/", method = RequestMethod.GET)
    public String delete(@PathVariable("id") long id, @RequestHeader(value = "referer") String referer) {
        submissionService.deleteSubmissionDisease(id);
        return "redirect:" + referer;
    }
}
