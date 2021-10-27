package org.dulab.adapcompounddb.site.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.controllers.forms.FilterForm;
import org.dulab.adapcompounddb.site.controllers.forms.FilterOptions;
import org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.SubmissionTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.Future;

@Controller
public class GroupSearchController extends BaseController {

    private static final Logger LOGGER = LogManager.getLogger(GroupSearchController.class);

    private final GroupSearchService groupSearchService;
    private final SubmissionService submissionService;
    private final SubmissionTagService submissionTagService;
    private FilterOptions filterOptions;
    private Future<Void> asyncResult;

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
        SortedMap<Long, String> submissions = submissionService.findUserPrivateSubmissions(this.getCurrentUserPrincipal());
        submissions.put(0L, "Public");

        filterOptions = new FilterOptions(speciesList, sourceList, diseaseList, submissions);
        model.addAttribute("filterOptions", filterOptions);
    }

    @RequestMapping(value = "/file/group_search/", method = RequestMethod.GET)
    public String groupSearch(@RequestParam Optional<Boolean> withOntologyLevels, HttpSession session, Model model,
                              @Valid FilterForm form) {

        session.removeAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);

        final Submission submission = Submission.from(session);
        if (submission == null) {
            return "redirect:/file/upload/";
        }

        return groupSearchGet(withOntologyLevels.orElse(false), submission, model, form);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/group_search/", method = RequestMethod.GET)
    public String groupSearch(@PathVariable("submissionId") long submissionId,
                              @RequestParam Optional<Boolean> withOntologyLevels, Model model, @Valid FilterForm form,
                              HttpSession session) {
        session.removeAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);
        groupSearchService.setProgress(0F);

        Submission submission = submissionService.findSubmission(submissionId);
        return groupSearchGet(withOntologyLevels.orElse(false), submission, model, form);
    }

    public String groupSearchGet(boolean withOntologyLevels, Submission submission, Model model, FilterForm form) {

        FilterOptions filterOptions = getFilterOptions(getChromatographyTypes(submission));
        model.addAttribute("filterOptions", filterOptions);

        form.setSubmissionIds(filterOptions.getSubmissions().keySet());
        form.setWithOntologyLevels(withOntologyLevels);
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

        FilterOptions filterOptions = getFilterOptions(getChromatographyTypes(submission));
        model.addAttribute("filterOptions", filterOptions);

        if (errors.hasErrors()) {
            return new ModelAndView("submission/group_search");
        }

        if (asyncResult != null && !asyncResult.isDone()) {
            asyncResult.cancel(true);
        }

        String species = form.getSpecies().equalsIgnoreCase("all") ? null : form.getSpecies();
        String source = form.getSource().equalsIgnoreCase("all") ? null : form.getSource();
        String disease = form.getDisease().equalsIgnoreCase("all") ? null : form.getDisease();

        asyncResult = groupSearchService.groupSearch(this.getCurrentUserPrincipal(), submission.getFiles(), session,
                form.getSubmissionIds(), species, source, disease, form.isWithOntologyLevels());

        return new ModelAndView("submission/group_search");
    }

    private Collection<ChromatographyType> getChromatographyTypes(Submission submission) {
        Collection<ChromatographyType> chromatographyTypes;
        if (submission.getId() == 0) {
            chromatographyTypes = new HashSet<>();
            for (File file : submission.getFiles()) {
                List<Spectrum> spectra = file.getSpectra();
                if (spectra == null) continue;
                for (Spectrum spectrum : spectra)
                    chromatographyTypes.add(spectrum.getChromatographyType());
            }
        } else {
            Map<Long, List<ChromatographyType>> map =
                    submissionService.findChromatographyTypes(Collections.singletonList(submission));
            chromatographyTypes = map.get(submission.getId());
        }
        return chromatographyTypes;
    }

    private FilterOptions getFilterOptions(Collection<ChromatographyType> chromatographyTypes) {
        List<String> speciesList = submissionTagService.findDistinctTagValuesByTagKey("species (common)");
        List<String> sourceList = submissionTagService.findDistinctTagValuesByTagKey("sample source");
        List<String> diseaseList = submissionTagService.findDistinctTagValuesByTagKey("disease");

        SortedMap<Long, String> submissions = new TreeMap<>();
        for (ChromatographyType chromatographyType : chromatographyTypes)
            submissions.putAll(
                    submissionService.findUserPrivateSubmissions(this.getCurrentUserPrincipal(), chromatographyType));
        submissions.put(0L, "Public");

        return new FilterOptions(speciesList, sourceList, diseaseList, submissions);
    }
}
