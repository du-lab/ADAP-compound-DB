package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.services.SpectrumSearchService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
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

    private final Map<ChromatographyType, SpectrumSearchService> spectrumSearchServiceMap;

    @Autowired
    public SearchController(UserPrincipalService userPrincipalService,
                            SubmissionService submissionService,
                            @Qualifier("spectrumServiceImpl") SpectrumService spectrumService,
                            @Qualifier("spectrumSearchServiceGCImpl") SpectrumSearchService gcSpectrumSearchService,
                            @Qualifier("spectrumSearchServiceLCImpl") SpectrumSearchService lcSpectrumSearchService) {

        this.userPrincipalService = userPrincipalService;
        this.submissionService = submissionService;
        this.spectrumService = spectrumService;

        this.spectrumSearchServiceMap = new HashMap<>();
        this.spectrumSearchServiceMap.put(ChromatographyType.GAS, gcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_POSITIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_NEGATIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_POS, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_NEG, lcSpectrumSearchService);
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("chromatographyTypes", ChromatographyType.values());
        model.addAttribute("submissionCategories", submissionService.findAllCategories());

        model.addAttribute("submissionCategoryTypes", SubmissionCategoryType.values());

        Map<SubmissionCategoryType, List<SubmissionCategory>> submissionCategoryMap = new HashMap<>();
        for (SubmissionCategory category : submissionService.findAllCategories())
            submissionCategoryMap
                    .computeIfAbsent(category.getCategoryType(), c -> new ArrayList<>())
                    .add(category);

        model.addAttribute("submissionCategoryMap", submissionCategoryMap);
    }

    @RequestMapping(
            value = "/submission/{submissionId:\\d+}/spectrum/{spectrumId:\\d+}/search/",
            method = RequestMethod.GET)
    public String search(@PathVariable("submissionId") long submissionId,
                         @PathVariable("spectrumId") int spectrumId,
                         HttpSession session, Model model) {

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);

        } catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/notfound/";
        }

        return searchGet(spectrum, UserPrincipal.from(session), model);
    }

    @RequestMapping(value = "/file/{fileIndex:\\d+}/{spectrumIndex:\\d+}/search/", method = RequestMethod.GET)
    public String search(@PathVariable("fileIndex") int fileIndex,
                         @PathVariable("spectrumIndex") int spectrumIndex,
                         HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        if (submission == null)
            return "redirect:/file/upload/";

        Spectrum spectrum = submission
                .getFiles()
                .get(fileIndex)
                .getSpectra()
                .get(spectrumIndex);

        return searchGet(spectrum, UserPrincipal.from(session), model);
    }

    @RequestMapping(value = "/spectrum/{spectrumId}/search/", method = RequestMethod.GET)
    public String search(@PathVariable("spectrumId") long spectrumId, HttpSession session, Model model) {

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);
        } catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/notfound/";
        }

        return searchGet(spectrum, UserPrincipal.from(session), model);
    }

    private String searchGet(Spectrum querySpectrum, UserPrincipal user, Model model) {

        SearchForm form = new SearchForm();
        form.setAvailableTags(submissionService.findAllTags());

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("searchForm", form);

        return "file/match";
    }

    @RequestMapping(
            value = "/submission/{submissionId:\\d+}/spectrum/{spectrumId:\\d+}/search",
            method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("submissionId") long submissionId,
            @PathVariable("spectrumId") int spectrumId,
                               HttpSession session, Model model, @Valid SearchForm searchForm, Errors errors) {

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);

        } catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView(new RedirectView("/notfound/"));
        }

        return searchPost(spectrum, UserPrincipal.from(session), searchForm, model, errors);
    }

    @RequestMapping(value = "/file/{fileIndex:\\d+}/{spectrumIndex:\\d+}/search/", method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("fileIndex") int fileIndex,
                               @PathVariable("spectrumIndex") int spectrumIndex,
                               HttpSession session, Model model, @Valid SearchForm form, Errors errors) {

        Submission submission = Submission.from(session);
        if (submission == null)
            return new ModelAndView(new RedirectView("/file/upload/"));

        Spectrum spectrum = submission
                .getFiles()
                .get(fileIndex)
                .getSpectra()
                .get(spectrumIndex);

        return searchPost(spectrum, UserPrincipal.from(session), form, model, errors);
    }

    @RequestMapping(value = "/spectrum/{spectrumId:\\d+}/search/", method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("spectrumId") long spectrumId,
                               HttpSession session, Model model, @Valid SearchForm form, Errors errors) {

        if (errors.hasErrors()) return new ModelAndView("file/match");

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);

        } catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView(new RedirectView("/notfound/"));
        }

        return searchPost(spectrum, UserPrincipal.from(session), form, model, errors);
    }

    private ModelAndView searchPost(Spectrum querySpectrum, UserPrincipal user,
                                    SearchForm form, @Valid Model model, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("querySpectrum", querySpectrum);
            return new ModelAndView("file/match");
        }

        SpectrumSearchService service =
                spectrumSearchServiceMap.get(querySpectrum.getChromatographyType());

        QueryParameters parameters = new QueryParameters();
        parameters.setScoreThreshold(form.isScoreThresholdCheck() ? form.getFloatScoreThreshold() : null);
        parameters.setMzTolerance(form.isScoreThresholdCheck() ? form.getMzTolerance() : null);
        parameters.setPrecursorTolerance(form.isMassToleranceCheck() ? form.getMassTolerance() : null);
        parameters.setRetTimeTolerance(form.isRetTimeToleranceCheck() ? form.getRetTimeTolerance() : null);

        String tags = form.getTags();
        parameters.setTags(
                tags != null && tags.length() > 0
                        ? new HashSet<>(Arrays.asList(tags.split(",")))
                        : null);

        List<SpectrumMatch> matches = service.search(querySpectrum, parameters);

        model.addAttribute("matches", matches);

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("form", form);

        return new ModelAndView("file/match");
    }


    private Spectrum getQuerySpectrum(long submissionId, int fileIndex, int spectrumIndex) {
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

        public void setMzTolerance(double mzTolerance) {
            this.mzTolerance = mzTolerance;
        }

        public boolean isScoreThresholdCheck() {
            return scoreThresholdCheck;
        }

        public void setScoreThresholdCheck(boolean scoreThresholdCheck) {
            this.scoreThresholdCheck = scoreThresholdCheck;
        }

        public int getScoreThreshold() {
            return scoreThreshold;
        }

        public void setScoreThreshold(int scoreThreshold) {
            this.scoreThreshold = scoreThreshold;
        }

        public double getFloatScoreThreshold() {
            return scoreThreshold / 1000.0;
        }

        public boolean isMassToleranceCheck() {
            return massToleranceCheck;
        }

        public void setMassToleranceCheck(boolean massToleranceCheck) {
            this.massToleranceCheck = massToleranceCheck;
        }

        public double getMassTolerance() {
            return massTolerance;
        }

        public void setMassTolerance(double massTolerance) {
            this.massTolerance = massTolerance;
        }

        public boolean isRetTimeToleranceCheck() {
            return retTimeToleranceCheck;
        }

        public void setRetTimeToleranceCheck(boolean retTimeToleranceCheck) {
            this.retTimeToleranceCheck = retTimeToleranceCheck;
        }

        public double getRetTimeTolerance() {
            return retTimeTolerance;
        }

        public void setRetTimeTolerance(double retTimeTolerance) {
            this.retTimeTolerance = retTimeTolerance;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public List<String> getAvailableTags() {
            return availableTags;
        }

        public void setAvailableTags(List<String> availableTags) {
            this.availableTags = availableTags;
        }
    }
}
