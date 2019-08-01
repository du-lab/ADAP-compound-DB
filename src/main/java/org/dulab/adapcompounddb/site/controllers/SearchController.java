package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;

@Controller
public class SearchController {

    private final UserPrincipalService userPrincipalService;
    private final SubmissionService submissionService;
    private final SpectrumService spectrumService;
    private final GroupSearchService groupSearchService;

    private final Map<ChromatographyType, SpectrumSearchService> spectrumSearchServiceMap;

    @Autowired
    public SearchController(final UserPrincipalService userPrincipalService,
                            final SubmissionService submissionService,
                            final GroupSearchService groupSearchService,
                            @Qualifier("spectrumServiceImpl") final SpectrumService spectrumService,
                            @Qualifier("spectrumSearchServiceGCImpl") final SpectrumSearchService gcSpectrumSearchService,
                            @Qualifier("spectrumSearchServiceLCImpl") final SpectrumSearchService lcSpectrumSearchService) {

        this.userPrincipalService = userPrincipalService;
        this.submissionService = submissionService;
        this.spectrumService = spectrumService;
        this.groupSearchService = groupSearchService;

        this.spectrumSearchServiceMap = new HashMap<>();
        this.spectrumSearchServiceMap.put(ChromatographyType.GAS, gcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_POSITIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_NEGATIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_POS, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_NEG, lcSpectrumSearchService);
    }

    @ModelAttribute
    public void addAttributes(final Model model) {
        model.addAttribute("chromatographyTypes", ChromatographyType.values());
        model.addAttribute("submissionCategories", submissionService.findAllCategories());

        model.addAttribute("submissionCategoryTypes", SubmissionCategoryType.values());

        final Map<SubmissionCategoryType, List<SubmissionCategory>> submissionCategoryMap = new HashMap<>();
        for (final SubmissionCategory category : submissionService.findAllCategories()) {
            submissionCategoryMap
                    .computeIfAbsent(category.getCategoryType(), c -> new ArrayList<>())
                    .add(category);
        }

        model.addAttribute("submissionCategoryMap", submissionCategoryMap);
    }


    @RequestMapping(
            value = "/submission/{submissionId:\\d+}/spectrum/{spectrumId:\\d+}/search/",
            method = RequestMethod.GET)
    public String search(@PathVariable("submissionId") final long submissionId,
                         @PathVariable("spectrumId") final int spectrumId,
                         final HttpSession session, final Model model) {

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);

        } catch (final EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/notfound/";
        }

        return searchGet(spectrum, UserPrincipal.from(session), model);
    }

    @RequestMapping(value = "/file/{fileIndex:\\d+}/{spectrumIndex:\\d+}/search/", method = RequestMethod.GET)
    public String search(@PathVariable("fileIndex") final int fileIndex,
                         @PathVariable("spectrumIndex") final int spectrumIndex,
                         final HttpSession session, final Model model) {

        final Submission submission = Submission.from(session);
        if (submission == null) {
            return "redirect:/file/upload/";
        }

        final Spectrum spectrum = submission
                .getFiles()
                .get(fileIndex)
                .getSpectra()
                .get(spectrumIndex);

        return searchGet(spectrum, UserPrincipal.from(session), model);
    }

    @RequestMapping(value = "/spectrum/{spectrumId}/search/", method = RequestMethod.GET)
    public String search(@PathVariable("spectrumId") final long spectrumId, final HttpSession session, final Model model) {

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);
        } catch (final EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/notfound/";
        }

        return searchGet(spectrum, UserPrincipal.from(session), model);
    }

    private String searchGet(final Spectrum querySpectrum, final UserPrincipal user, final Model model) {

        final SearchForm form = new SearchForm();
        form.setAvailableTags(submissionService.findAllTags());

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("searchForm", form);

        return "file/match";
    }

    @RequestMapping(
            value = "/submission/{submissionId:\\d+}/spectrum/{spectrumId:\\d+}/search",
            method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("submissionId") final long submissionId,
                               @PathVariable("spectrumId") final int spectrumId,
                               final HttpSession session, final Model model, @Valid final SearchForm searchForm,
                               final Errors errors) {

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);

        } catch (final EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView(new RedirectView("/notfound/"));
        }

        return searchPost(spectrum, searchForm, model, errors);
    }

    @RequestMapping(value = "/file/{fileIndex:\\d+}/{spectrumIndex:\\d+}/search/", method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("fileIndex") final int fileIndex,
                               @PathVariable("spectrumIndex") final int spectrumIndex,
                               final HttpSession session, final Model model, @Valid final SearchForm form,
                               final Errors errors) {

        final Submission submission = Submission.from(session);
        if (submission == null) {
            return new ModelAndView(new RedirectView("/file/upload/"));
        }

        final Spectrum spectrum = submission
                .getFiles()
                .get(fileIndex)
                .getSpectra()
                .get(spectrumIndex);

        return searchPost(spectrum, form, model, errors);
    }

    @RequestMapping(value = "/spectrum/{spectrumId:\\d+}/search/", method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("spectrumId") final long spectrumId,
                               final HttpSession session, final Model model, @Valid final SearchForm form,
                               final Errors errors) {

        if (errors.hasErrors()) {
            return new ModelAndView("file/match");
        }

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);

        } catch (final EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView(new RedirectView("/notfound/"));
        }

        return searchPost(spectrum, form, model, errors);
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
        model.addAttribute("searchForm", form);
        return "/group_search_results";
    }

    @RequestMapping(value = "/file/group_search_results/", method = RequestMethod.POST)
    public ModelAndView groupSearch(final HttpSession session, final Model model, @Valid final SearchForm form,
                                    final Errors errors) {
        return nonSubmittedGroupSearch(session, form, model, errors);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/group_search_results/", method = RequestMethod.POST)
    public ModelAndView groupSearch(@PathVariable("submissionId") final long submissionId, final HttpSession session,
                                    final Model model, @Valid final SearchForm form, final Errors errors) {
        final Submission submission = submissionService.findSubmission(submissionId);
        return groupSearchPost(session, submission, form, model, errors);
    }


    //TODO: Remove this funciton and put the code into the corresponding groupSearcg()
    private ModelAndView nonSubmittedGroupSearch(final HttpSession session,
                                                 final SearchForm form, @Valid final Model model, final Errors errors) {
        final Submission submission = Submission.from(session);

        if (errors.hasErrors()) {
            return new ModelAndView("file/match");
        }

        final QueryParameters parameters = getParameters(form);

        new Thread(() -> groupSearchService.nonSubmittedGroupSearch(submission, session, parameters)).start();

        model.addAttribute("form", form);
        return new ModelAndView("group_search_results");
    }

    //TODO: Remove this funciton and put the code into the corresponding groupSearcg()
    private ModelAndView groupSearchPost(final HttpSession session,
                                         Submission submission,
                                         final SearchForm form, @Valid final Model model, final Errors errors) {
        if (errors.hasErrors()) {
            return new ModelAndView("file/match");
        }

        final QueryParameters parameters = getParameters(form);

        new Thread(() -> groupSearchService.groupSearch(submission.getId(), session, parameters)).start();

        model.addAttribute("form", form);
        return new ModelAndView("group_search_results");
    }

    private QueryParameters getParameters(final SearchForm form) {
        final QueryParameters parameters = new QueryParameters();
        final String tags = form.getTags();
        parameters.setScoreThreshold(form.isScoreThresholdCheck() ? form.getFloatScoreThreshold() : null);
        parameters.setMzTolerance(form.isScoreThresholdCheck() ? form.getMzTolerance() : null);
        parameters.setPrecursorTolerance(form.isMassToleranceCheck() ? form.getMassTolerance() : null);
        parameters.setRetTimeTolerance(form.isRetTimeToleranceCheck() ? form.getRetTimeTolerance() : null);
        parameters.setTags(tags != null && tags.length() > 0 ? new HashSet<>(Arrays.asList(tags.split(","))) : null);
        return parameters;
    }

    private ModelAndView searchPost(final Spectrum querySpectrum,
                                    final SearchForm form, @Valid final Model model, final Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("querySpectrum", querySpectrum);
            return new ModelAndView("file/match");
        }

        final SpectrumSearchService service =
                spectrumSearchServiceMap.get(querySpectrum.getChromatographyType());

        final QueryParameters parameters = getParameters(form);

        final List<SpectrumMatch> matches = service.search(querySpectrum, parameters);

        model.addAttribute("matches", matches);

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("form", form);

        return new ModelAndView("file/match");
    }


    private Spectrum getQuerySpectrum(final long submissionId, final int fileIndex, final int spectrumIndex) {
        return submissionService.findSubmission(submissionId)
                .getFiles()
                .get(fileIndex)
                .getSpectra()
                .get(spectrumIndex);
    }

    public static class SearchForm {

        private boolean scoreThresholdCheck = true;

        @Min(value = 0, message = "M/z tolerance must be positive.")
        private double mzTolerance = 0.01;

        @Min(value = 0, message = "Matching score threshold must be between 0 and 1000.")
        @Max(value = 1000, message = "Matching score threshold must be between 0 and 1000.")
        private int scoreThreshold = 750;

        private boolean massToleranceCheck = true;

        @Min(value = 0, message = "M/z tolerance must be positive.")
        private double massTolerance = 0.01;

        private boolean retTimeToleranceCheck = false;

        private double retTimeTolerance = 0.5;

        private String tags;

        private List<String> availableTags;

        // *******************************
        // ***** Getters and Setters *****
        // *******************************

        public double getMzTolerance() {
            return mzTolerance;
        }

        public void setMzTolerance(final double mzTolerance) {
            this.mzTolerance = mzTolerance;
        }

        public boolean isScoreThresholdCheck() {
            return scoreThresholdCheck;
        }

        public void setScoreThresholdCheck(final boolean scoreThresholdCheck) {
            this.scoreThresholdCheck = scoreThresholdCheck;
        }

        public int getScoreThreshold() {
            return scoreThreshold;
        }

        public void setScoreThreshold(final int scoreThreshold) {
            this.scoreThreshold = scoreThreshold;
        }

        public double getFloatScoreThreshold() {
            return scoreThreshold / 1000.0;
        }

        public boolean isMassToleranceCheck() {
            return massToleranceCheck;
        }

        public void setMassToleranceCheck(final boolean massToleranceCheck) {
            this.massToleranceCheck = massToleranceCheck;
        }

        public double getMassTolerance() {
            return massTolerance;
        }

        public void setMassTolerance(final double massTolerance) {
            this.massTolerance = massTolerance;
        }

        public boolean isRetTimeToleranceCheck() {
            return retTimeToleranceCheck;
        }

        public void setRetTimeToleranceCheck(final boolean retTimeToleranceCheck) {
            this.retTimeToleranceCheck = retTimeToleranceCheck;
        }

        public double getRetTimeTolerance() {
            return retTimeTolerance;
        }

        public void setRetTimeTolerance(final double retTimeTolerance) {
            this.retTimeTolerance = retTimeTolerance;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(final String tags) {
            this.tags = tags;
        }

        public List<String> getAvailableTags() {
            return availableTags;
        }

        public void setAvailableTags(final List<String> availableTags) {
            this.availableTags = availableTags;
        }
    }
}
