package org.dulab.adapcompounddb.site.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.SpectrumSearchService;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Controller
public class SearchController extends BaseController {

    private final UserPrincipalService userPrincipalService;
    private final SubmissionService submissionService;
    private final SpectrumService spectrumService;

    private final Map<ChromatographyType, SpectrumSearchService> spectrumSearchServiceMap;

    @Autowired
    public SearchController(final UserPrincipalService userPrincipalService,
            final SubmissionService submissionService,
            @Qualifier("spectrumServiceImpl") final SpectrumService spectrumService,
            @Qualifier("spectrumSearchServiceGCImpl") final SpectrumSearchService gcSpectrumSearchService,
            @Qualifier("spectrumSearchServiceLCImpl") final SpectrumSearchService lcSpectrumSearchService) {

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

        final Submission submission = getSubmissionFromSession(session);
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

    @RequestMapping(value = "/search/{submissionId}/", method = RequestMethod.GET)
    public String searchAllSpectrum(@PathVariable(name="submissionId", required=false) final long submissionId, final HttpSession session, final Model model) {
        final SearchForm form = new SearchForm();
        form.setAvailableTags(submissionService.findAllTags());
        Submission submission = null;
        if(submissionId == 0) {
            submission = getSubmissionFromSession(session);
        } else {
            submission = submissionService.findSubmission(submissionId);
        }

        if(submission == null) {
            return "redirect:/";
        }

        model.addAttribute("searchForm", form);
        model.addAttribute("submission", submission);
        return "submission/match";
    }

    /*@RequestMapping(value = "/search/{submissionId:\\d+}/", method = RequestMethod.POST)
    public ModelAndView searchAllSpectrum(@PathVariable("submissionId") final long submissionId,
            final HttpSession session, final Model model, @Valid final SearchForm form, final Errors errors) {

        if (errors.hasErrors()) {
            return new ModelAndView("file/match");
        }
        final Submission submission = submissionService.findSubmission(submissionId);

        return searchPost(submission, UserPrincipal.from(session), form, model, errors);
    }*/

    @RequestMapping(value = "/search-spectra")
    public String searchSpectraBySubmission(final HttpSession session, final SearchParams searchParams, final Model model) {
        getSpectraBySearchParams(session, searchParams);
        model.addAttribute("submissionId", searchParams.getSubmissionId());
        return "../includes/search_spectra";
    }

    @ResponseBody
    @RequestMapping(value = "/search-spectra-data", produces="application/json")
    public String getSearchSpectraData(final HttpSession session, final SearchParams searchParams, @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length) {

        final List<SpectrumMatch> matches = getSpectraBySearchParams(session, searchParams);
        final int size = matches.size();
        final DataTableResponse response = new DataTableResponse();
        response.setDataList(matches.subList(start, start + length > size ? size : start + length));
        response.setRecordsFiltered(new Long(size));
        response.setRecordsTotal(new Long(size));
        final ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "{}";
        try {
            jsonString = objectMapper.writeValueAsString(response);
        } catch (final JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public List<SpectrumMatch> getSpectraBySearchParams(final HttpSession session, final SearchParams searchParams) {
        //        return new ArrayList<>();
        final Map<SearchParams, List<SpectrumMatch>> cachedSpectraSearchResults = getSearchResultCache(session);
        List<SpectrumMatch> matches = null;
        if(cachedSpectraSearchResults != null) {
            matches = cachedSpectraSearchResults.get(searchParams);
        }
        if(matches == null) {

            final QueryParameters parameters = new QueryParameters();
            parameters.setScoreThreshold(searchParams.getIsScoreThreshold() ? searchParams.getScoreThreshold() : null);
            parameters.setMzTolerance(searchParams.getIsScoreThreshold() ? searchParams.getMzTolerance() : null);
            parameters.setPrecursorTolerance(searchParams.getIsMassTolerance() ? searchParams.getMassTolerance() : null);
            parameters.setRetTimeTolerance(searchParams.getIsRetTimeTolerance() ? searchParams.getRetTimeTolerance() : null);

            final String tags = searchParams.getTags();
            parameters.setTags(
                    tags != null && tags.length() > 0 ?
                            new HashSet<>(Arrays.asList(tags.split(",")))
                            : null);

            final Set<Spectrum> spectra = new HashSet<>();
            if(searchParams.getSubmissionId() > 0) {
                spectra.addAll(spectrumService.findSpectrumBySubmissionId(searchParams.getSubmissionId()));
            } else {
                getSubmissionFromSession(session).getFiles().forEach(f -> spectra.addAll(f.getSpectra()));
            }
            final SpectrumSearchService service =
                    spectrumSearchServiceMap.get(spectra.stream().findFirst().get().getChromatographyType());
            final Set<Long> spectraIdList = spectra.stream().map(s -> s.getId()).collect(Collectors.toSet());
            parameters.addExludeSpectra(spectraIdList);
            List<SpectrumMatch> currentMatches = null;

            matches = new ArrayList<>();
            SpectrumMatch spectrumMatch = null;
            for(final Spectrum s: spectra) {
                currentMatches = service.search(s, parameters);
                if(currentMatches != null && currentMatches.size() > 0) {
                    spectrumMatch =currentMatches.get(0);
                } else {
                    spectrumMatch = new SpectrumMatch();
                    spectrumMatch.setQuerySpectrumName(s.getName());
                }
                matches.add(spectrumMatch);
            }
            addSearchResultsToCache(session, searchParams, matches);
        }
        return matches;
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
            final HttpSession session, final Model model, @Valid final SearchForm searchForm, final Errors errors) {

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);

        } catch (final EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView(new RedirectView("/notfound/"));
        }

        return searchPost(spectrum, UserPrincipal.from(session), searchForm, model, errors);
    }

    @RequestMapping(value = "/file/{fileIndex:\\d+}/{spectrumIndex:\\d+}/search/", method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("fileIndex") final int fileIndex,
            @PathVariable("spectrumIndex") final int spectrumIndex,
            final HttpSession session, final Model model, @Valid final SearchForm form, final Errors errors) {

        final Submission submission = getSubmissionFromSession(session);
        if (submission == null) {
            return new ModelAndView(new RedirectView("/file/upload/"));
        }

        final Spectrum spectrum = submission
                .getFiles()
                .get(fileIndex)
                .getSpectra()
                .get(spectrumIndex);

        return searchPost(spectrum, UserPrincipal.from(session), form, model, errors);
    }

    @RequestMapping(value = "/spectrum/{spectrumId:\\d+}/search/", method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("spectrumId") final long spectrumId,
            final HttpSession session, final Model model, @Valid final SearchForm form, final Errors errors) {

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

        return searchPost(spectrum, UserPrincipal.from(session), form, model, errors);
    }

    private ModelAndView searchPost(final Spectrum querySpectrum, final UserPrincipal user,
            final SearchForm form, @Valid final Model model, final Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("querySpectrum", querySpectrum);
            return new ModelAndView("file/match");
        }

        final SpectrumSearchService service =
                spectrumSearchServiceMap.get(querySpectrum.getChromatographyType());

        final QueryParameters parameters = new QueryParameters();
        parameters.setScoreThreshold(form.isScoreThresholdCheck() ? form.getFloatScoreThreshold() : null);
        parameters.setMzTolerance(form.isScoreThresholdCheck() ? form.getMzTolerance() : null);
        parameters.setPrecursorTolerance(form.isMassToleranceCheck() ? form.getMassTolerance() : null);
        parameters.setRetTimeTolerance(form.isRetTimeToleranceCheck() ? form.getRetTimeTolerance() : null);

        final String tags = form.getTags();
        parameters.setTags(
                tags != null && tags.length() > 0
                ? new HashSet<>(Arrays.asList(tags.split(",")))
                        : null);

        final List<SpectrumMatch> matches = service.search(querySpectrum, parameters);

        model.addAttribute("matches", matches);

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("form", form);

        return new ModelAndView("file/match");
    }

    private ModelAndView searchPost(final Submission submission, final UserPrincipal user,
            final SearchForm form, @Valid final Model model, final Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("submission", submission);
            return new ModelAndView("file/match");
        }

        final SpectrumSearchService service =
                spectrumSearchServiceMap.get(submissionService.getChromatographyTypeBySubmissionId(submission.getId()));

        final QueryParameters parameters = new QueryParameters();
        parameters.setScoreThreshold(form.isScoreThresholdCheck() ? form.getFloatScoreThreshold() : null);
        parameters.setMzTolerance(form.isScoreThresholdCheck() ? form.getMzTolerance() : null);
        parameters.setPrecursorTolerance(form.isMassToleranceCheck() ? form.getMassTolerance() : null);
        parameters.setRetTimeTolerance(form.isRetTimeToleranceCheck() ? form.getRetTimeTolerance() : null);

        final String tags = form.getTags();
        parameters.setTags(
                tags != null && tags.length() > 0 ?
                        new HashSet<>(Arrays.asList(tags.split(",")))
                        : null);

        final Set<Spectrum> spectra = new HashSet<>(spectrumService.findSpectrumBySubmissionId(submission.getId()));
        Set<Long> spectraIdList = spectra.stream().map(s -> s.getId()).collect(Collectors.toSet());
        parameters.addExludeSpectra(spectraIdList);
        List<SpectrumMatch> currentMatches = null;

        List<SpectrumMatch> matches = null;
        matches = new ArrayList<>();
        for(final Spectrum s: spectra) {
            currentMatches = service.search(s, parameters);
            spectraIdList = currentMatches.stream().map(m -> m.getMatchSpectrum().getId()).collect(Collectors.toSet());
            parameters.addExludeSpectra(spectraIdList);
            matches.addAll(currentMatches);
        }

        model.addAttribute("matches", matches);

        model.addAttribute("submission", submission);
        model.addAttribute("form", form);

        return new ModelAndView("submission/match");
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


@Getter
@Setter
class SearchParams {
    Long submissionId;
    Boolean isScoreThreshold;
    Double scoreThreshold;
    Double mzTolerance;
    Boolean isMassTolerance;
    Double massTolerance;
    Boolean isRetTimeTolerance;
    Double retTimeTolerance;
    String tags;

    @Override
    public int hashCode() {
        final String hashString = submissionId.toString() +
                isScoreThreshold.toString() +
                (isScoreThreshold ? scoreThreshold.toString() : "") +
                (isScoreThreshold ? mzTolerance.toString() : "") +
                isMassTolerance.toString() + // false
                (isMassTolerance ? massTolerance.toString() : "") +
                isRetTimeTolerance.toString() +
                (isRetTimeTolerance ? retTimeTolerance.toString() : "") +
                tags;
        return hashString.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return this.hashCode() == obj.hashCode();
    }
}