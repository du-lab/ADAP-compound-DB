package org.dulab.adapcompounddb.site.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchForm;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.services.GroupSearchService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class GroupSearchController {

    private static final Logger LOGGER = LogManager.getLogger(GroupSearchController.class);

    private final GroupSearchService groupSearchService;
    private final SubmissionService submissionService;

    @Autowired
    public GroupSearchController(final GroupSearchService groupSearchService, SubmissionService submissionService) {
        this.groupSearchService = groupSearchService;
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/file/group_search/", method = RequestMethod.GET)
    public String groupSearch(final HttpSession session, final Model model, @Valid final SearchForm form) {
        session.removeAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);

        final Submission submission = Submission.from(session);
        if (submission == null) {
            return "redirect:/file/upload/";
        }
        model.addAttribute("searchForm", form);
        return "submission/group_search";
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/group_search/", method = RequestMethod.GET)
    public String groupSearch(final Model model, @Valid final SearchForm form, final HttpSession session) {
        session.removeAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);
        groupSearchService.setProgress(0F);
        model.addAttribute("searchForm", form);
        return "submission/group_search";
    }

    @RequestMapping(value = "/file/group_search/", method = RequestMethod.POST)
    public ModelAndView groupSearch(final HttpSession session, final Model model, @Valid final SearchForm form,
                                    final Errors errors) {
        final Submission submission = Submission.from(session);
        groupSearchService.setProgress(0F);
        if (errors.hasErrors()) {
            return new ModelAndView("submission/group_search");
        }
        final QueryParameters parameters = ControllerUtils.getParameters(form);
        new Thread(() -> groupSearchService.groupSearch(submission, session, parameters)).start();
        model.addAttribute("form", form);
        return new ModelAndView("submission/group_search");
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/group_search/", method = RequestMethod.POST)
    public ModelAndView groupSearch(@PathVariable("submissionId") final long submissionId, final HttpSession session,
                                    final Model model, @Valid final SearchForm form, final Errors errors) {
        if (errors.hasErrors()) {
            return new ModelAndView("submission/group_search");
        }
        final QueryParameters parameters = ControllerUtils.getParameters(form);
        Submission submission = submissionService.findSubmission(submissionId);
        new Thread(() -> {
            try {
                groupSearchService.groupSearch(submission, session, parameters);
            } catch (Exception e) {
                LOGGER.error("Error during the group search: " + e.getMessage(), e);
            }
        }).start();
        model.addAttribute("form", form);
        return new ModelAndView("submission/group_search");
    }
}
