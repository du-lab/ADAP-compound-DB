package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
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
public class SubmissionCategoryController {

    private static final String SESSION_ATTRIBUTE_REDIRECT_URL = "PRIOR_URL";

    private final SubmissionService submissionService;

    @Autowired
    public SubmissionCategoryController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/categories/{categoryType}/", method = RequestMethod.GET)
    public String view(@PathVariable("categoryType") SubmissionCategoryType categoryType, Model model) {

        List<ControllerUtils.CategoryWithSubmissionCount> categories = submissionService.findAllCategories(categoryType)
                .stream()
                .map(s -> new ControllerUtils.CategoryWithSubmissionCount(
                        s,
                        submissionService.countSubmissionsByCategoryId(s.getId())))
                .collect(Collectors.toList());

        model.addAttribute("categories", categories);
        return "submission/view_categories";
    }

    @RequestMapping(value = "/categories/*/add/", method = RequestMethod.GET)
    public String add(HttpSession session, Model model, @RequestHeader(value = "referer") String referer) {
        session.setAttribute(SESSION_ATTRIBUTE_REDIRECT_URL, referer);
        model.addAttribute("categoryForm", new ControllerUtils.CategoryForm());
        return "submission/edit_category";
    }

    @RequestMapping(value = "/categories/{categoryType}/add/", method = RequestMethod.POST)
    public String add(@PathVariable("categoryType") SubmissionCategoryType categoryType,
                      HttpSession session, Model model, @Valid ControllerUtils.CategoryForm form, Errors errors) {

        if (errors.hasErrors())
            return "submission/edit_source";

        SubmissionCategory category = new SubmissionCategory();
        category.setCategoryType(categoryType);

        return save(session, model, category, form);
    }

    @RequestMapping(value = "/categories/*/{id:\\d+}/", method = RequestMethod.GET)
    public String edit(@PathVariable("id") long id, HttpSession session, Model model,
                       @RequestHeader(value = "referer", required = false) String referer) {

        SubmissionCategory category = submissionService
                .findSubmissionCategory(id)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Submission Category with ID = %d cannot be found.", id)));

        session.setAttribute(SESSION_ATTRIBUTE_REDIRECT_URL, referer);

        ControllerUtils.CategoryForm form = new ControllerUtils.CategoryForm();
        form.setName(category.getName());
        form.setDescription(category.getDescription());
        model.addAttribute("categoryForm", form);

        return "submission/edit_category";
    }

    @RequestMapping(value = "/categories/*/{id:\\d+}/", method = RequestMethod.POST)
    public String edit(@PathVariable("id") long id, HttpSession session, Model model,
                       @Valid ControllerUtils.CategoryForm form, Errors errors) {

        if (errors.hasErrors())
            return "submission/edit_source";

        SubmissionCategory category = submissionService
                .findSubmissionCategory(id)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Submission Category with ID = %d cannot be found.", id)));

        return save(session, model, category, form);
    }


    public String save(HttpSession session, Model model,
                       SubmissionCategory category, ControllerUtils.CategoryForm form) {

        category.setName(form.getName());
        category.setDescription(form.getDescription());

        try {
            submissionService.saveSubmissionCategory(category);
        } catch (ConstraintViolationException e) {
            model.addAttribute("violationErrors", e.getConstraintViolations());
            return "submission/edit_category";
        }

        String redirectUrl = session.getAttribute(SESSION_ATTRIBUTE_REDIRECT_URL).toString();
        if (redirectUrl == null)
            redirectUrl = "/categories/" + category.getCategoryType().name();

        return "redirect:" + redirectUrl;
    }

    @RequestMapping(value = "/categories/*/{id:\\d+}/delete/", method = RequestMethod.GET)
    public String delete(@PathVariable("id") long id, @RequestHeader(value = "referer") String referer) {
        submissionService.deleteSubmissionCategory(id);
        return "redirect:" + referer;
    }
}
