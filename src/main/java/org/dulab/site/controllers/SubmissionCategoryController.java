package org.dulab.site.controllers;

import org.dulab.models.SubmissionCategory;
import org.dulab.models.UserPrincipal;
import org.dulab.site.services.SubmissionService;
import org.dulab.validation.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/submissioncategory/")
public class SubmissionCategoryController {

    private final SubmissionService submissionService;

    @Autowired
    public SubmissionCategoryController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String list(Model model) {

        Map<SubmissionCategory, Long> submissionCategories = submissionService.getAllSubmissionCategories()
                .stream()
                .collect(Collectors
                        .toMap(x -> x, x -> submissionService.getSubmissionCountByCategory(x.getId())));

        model.addAttribute("submissionCategories", submissionCategories);
        return "submissioncategory/list";
    }

    @RequestMapping(value = "{id:\\d+}/", method = RequestMethod.GET)
    public String view(@PathVariable("id") long id, Model model) {

        SubmissionCategory category;
        try {
            category = submissionService.getSubmissionCategory(id);
        }
        catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/submissioncategory/notfound";
        }

        Form form = new Form();
        form.setName(category.getName());
        form.setDescription(category.getDescription());
        model.addAttribute("form", form);
        return "submissioncategory/edit";
    }

    @RequestMapping(value = "add/", method = RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute("form", new Form());
        return "submissioncategory/edit";
    }

    @RequestMapping(value = "add/", method = RequestMethod.POST)
    public String add(HttpSession session, Model model, @Valid Form form, Errors errors) {
        if (errors.hasErrors())
            return "submissioncategory/edit";

        SubmissionCategory category = new SubmissionCategory();
        category.setUser(UserPrincipal.from(session));

        return save(category, model, form);
    }

    @RequestMapping(value = "{id:\\d+}/", method = RequestMethod.POST)
    public String add(@PathVariable("id") long id, Model model, @Valid Form form, Errors errors) {
        if (errors.hasErrors())
            return "submissioncategory/edit";

        SubmissionCategory category;
        try {
            category = submissionService.getSubmissionCategory(id);
        }
        catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/submissioncategory/notfound";
        }

        return save(category, model, form);
    }

    public String save(SubmissionCategory category, Model model, Form form) {

        category.setName(form.getName());
        category.setDescription(form.getDescription());

        try {
            submissionService.saveSubmissionCategory(category);
        }
        catch (ConstraintViolationException e) {
            model.addAttribute("violationErrors", e.getConstraintViolations());
            return "submissioncategory/edit";
        }

        return "redirect:/submissioncategory/";
    }


    public static class Form {

        @NotBlank(message = "The field Name is required.")
        private String name;

        @NotBlank(message = "The field Description is required.")
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
}
