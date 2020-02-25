package org.dulab.adapcompounddb.site.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchForm;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.controllers.forms.FilterForm;
import org.dulab.adapcompounddb.site.controllers.forms.FilterOptions;
import org.dulab.adapcompounddb.site.services.GroupSearchService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.SubmissionTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.Filter;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.Future;

@Controller
public class GroupSearchController {

    private static final Logger LOGGER = LogManager.getLogger(GroupSearchController.class);

    private final GroupSearchService groupSearchService;
    private final SubmissionService submissionService;
    private final SubmissionTagService submissionTagService;
    private Thread thread = null;

    @Autowired
    public GroupSearchController(GroupSearchService groupSearchService,
                                 SubmissionService submissionService,
                                 SubmissionTagService submissionTagService) {

        this.groupSearchService = groupSearchService;
        this.submissionService = submissionService;
        this.submissionTagService = submissionTagService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        List<String> speciesList = submissionTagService.findDistinctTagValuesByTagKey("species (common)");
        List<String> sourceList = submissionTagService.findDistinctTagValuesByTagKey("sample source");
        List<String> diseaseList = submissionTagService.findDistinctTagValuesByTagKey("disease");

        model.addAttribute("filterOptions", new FilterOptions(speciesList, sourceList, diseaseList));
    }

    @RequestMapping(value = "/file/group_search/", method = RequestMethod.GET)
    public String groupSearch(final HttpSession session, final Model model, @Valid final FilterForm form) {
        session.removeAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);

        final Submission submission = Submission.from(session);
        if (submission == null) {
            return "redirect:/file/upload/";
        }
        model.addAttribute("filterForm", form);
        return "submission/group_search";
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/group_search/", method = RequestMethod.GET)
    public String groupSearch(final Model model, @Valid final FilterForm form, final HttpSession session) {
        session.removeAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);
        groupSearchService.setProgress(0F);
        model.addAttribute("filterForm", form);
        return "submission/group_search";
    }

    @RequestMapping(value = "/file/group_search/", method = RequestMethod.POST)
    public ModelAndView groupSearch(final HttpSession session, final Model model, @Valid final FilterForm form,
                                    final Errors errors) {

        final Submission submission = Submission.from(session);
        return groupSearchPost(session, model, form, errors, submission);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/group_search/", method = RequestMethod.POST)
    public ModelAndView groupSearch(@PathVariable("submissionId") final long submissionId, final HttpSession session,
                                    final Model model, @Valid final FilterForm form, final Errors errors) {

        Submission submission = submissionService.findSubmission(submissionId);
        return groupSearchPost(session, model, form, errors, submission);
    }

    private ModelAndView groupSearchPost(
            HttpSession session, Model model, @Valid FilterForm form, Errors errors, Submission submission) {

        if (errors.hasErrors()) {
            return new ModelAndView("submission/group_search");
        }

        if (thread != null)
            thread.interrupt();

        thread = new Thread(() -> {
            try {
                groupSearchService.groupSearch(submission, session, form.getSpecies(), form.getSource(), form.getDisease());
            } catch (Exception e) {
                LOGGER.error("Error during the group search: " + e.getMessage(), e);
            }
        });
        thread.start();

        model.addAttribute("filterForm", form);

        return new ModelAndView("submission/group_search");
    }
}
