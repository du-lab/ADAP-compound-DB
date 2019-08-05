package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchForm;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.services.GroupSearchService;
import org.dulab.adapcompounddb.site.services.SpectrumSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class GroupSearchController {
    private final GroupSearchService groupSearchService;
    private final Map<ChromatographyType, SpectrumSearchService> spectrumSearchServiceMap;

    @Autowired
    public GroupSearchController(final GroupSearchService groupSearchService,
                        @Qualifier("spectrumSearchServiceGCImpl") final SpectrumSearchService gcSpectrumSearchService,
                        @Qualifier("spectrumSearchServiceLCImpl") final SpectrumSearchService lcSpectrumSearchService) {
        this.groupSearchService = groupSearchService;
        this.spectrumSearchServiceMap = new HashMap<>();
        this.spectrumSearchServiceMap.put(ChromatographyType.GAS, gcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_POSITIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_NEGATIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_POS, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_NEG, lcSpectrumSearchService);
    }

    @RequestMapping(value = "/file/group_search_results/", method = RequestMethod.GET)
    public String groupSearch(final HttpSession session, final Model model, @Valid final SearchForm form) {
        session.removeAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);

        final Submission submission = Submission.from(session);
        if (submission == null) {
            return "/file/upload/";
        }
        model.addAttribute("searchForm", form);
        return "/group_search_results";
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/group_search_results/", method = RequestMethod.GET)
    public String groupSearch(final Model model, @Valid final SearchForm form, final HttpSession session) {
        session.removeAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);
        groupSearchService.setProgress(0F);
        model.addAttribute("searchForm", form);
        return "/group_search_results";
    }

    @RequestMapping(value = "/file/group_search_results/", method = RequestMethod.POST)
    public ModelAndView groupSearch(final HttpSession session, final Model model, @Valid final SearchForm form,
                                    final Errors errors) {
        final Submission submission = Submission.from(session);
        groupSearchService.setProgress(0F);
        if (errors.hasErrors()) {
            return new ModelAndView("file/match");
        }
        final QueryParameters parameters = ControllerUtils.getParameters(form);
        new Thread(() -> groupSearchService.nonSubmittedGroupSearch(submission, session, parameters)).start();
        model.addAttribute("form", form);
        return new ModelAndView("group_search_results");
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/group_search_results/", method = RequestMethod.POST)
    public ModelAndView groupSearch(@PathVariable("submissionId") final long submissionId, final HttpSession session,
                                    final Model model, @Valid final SearchForm form, final Errors errors) {
        if (errors.hasErrors()) {
            return new ModelAndView("file/match");
        }
        final QueryParameters parameters = ControllerUtils.getParameters(form);
        new Thread(() -> groupSearchService.groupSearch(submissionId, session, parameters)).start();
        model.addAttribute("form", form);
        return new ModelAndView("group_search_results");
    }
}
