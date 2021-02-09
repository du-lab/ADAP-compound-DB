package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.controllers.forms.FilterForm;
import org.dulab.adapcompounddb.site.controllers.forms.FilterOptions;
import org.dulab.adapcompounddb.site.services.*;
import org.dulab.adapcompounddb.site.services.search.IndividualSearchService;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
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
import java.util.*;

@Controller
public class IndividualSearchController extends BaseController {

    private final SubmissionService submissionService;
    private final SpectrumService spectrumService;
    private final SubmissionTagService submissionTagService;
    private final IndividualSearchService individualSearchService;

    @Autowired
    public IndividualSearchController(SubmissionService submissionService,
                                      SubmissionTagService submissionTagService,
                                      @Qualifier("spectrumServiceImpl") SpectrumService spectrumService,
                                      @Qualifier("spectrumSearchServiceImpl") IndividualSearchService individualSearchService) {

        this.submissionService = submissionService;
        this.spectrumService = spectrumService;
        this.submissionTagService = submissionTagService;
        this.individualSearchService = individualSearchService;
    }

    @ModelAttribute
    public void addAttributes(final Model model) {
//        model.addAttribute("chromatographyTypes", ChromatographyType.values());
//        model.addAttribute("submissionCategories", submissionService.findAllCategories());

//        model.addAttribute("submissionCategoryTypes", SubmissionCategoryType.values());

//        final Map<SubmissionCategoryType, List<SubmissionCategory>> submissionCategoryMap = new HashMap<>();
//        for (final SubmissionCategory category : submissionService.findAllCategories()) {
//            submissionCategoryMap
//                    .computeIfAbsent(category.getCategoryType(), c -> new ArrayList<>())
//                    .add(category);
//        }
//        model.addAttribute("submissionCategoryMap", submissionCategoryMap);

//        List<String> speciesList = submissionTagService.findDistinctTagValuesByTagKey("species (common)");
//        List<String> sourceList = submissionTagService.findDistinctTagValuesByTagKey("sample source");
//        List<String> diseaseList = submissionTagService.findDistinctTagValuesByTagKey("disease");
//
//        model.addAttribute("filterOptions", new FilterOptions(speciesList, sourceList, diseaseList));
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

        return searchGet(spectrum, model);
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

        return searchGet(spectrum, model);
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

        return searchGet(spectrum, model);
    }

    private String searchGet(final Spectrum querySpectrum, final Model model) {

        model.addAttribute("querySpectrum", querySpectrum);

        FilterOptions filterOptions = getFilterOptions(querySpectrum.getChromatographyType());
        model.addAttribute("filterOptions", filterOptions);

        FilterForm form = new FilterForm();
        form.setSubmissionIds(filterOptions.getSubmissions().keySet());
        model.addAttribute("filterForm", form);

        return "submission/spectrum/search";
    }

    @RequestMapping(
            value = "/submission/*/spectrum/{spectrumId:\\d+}/search/",
            method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("spectrumId") final int spectrumId,
                               final Model model,
                               @Valid final FilterForm filterForm,
                               final Errors errors) {

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);

        } catch (final EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView(new RedirectView("/notfound/"));
        }

        return searchPost(spectrum, filterForm, model, errors);
    }

    @RequestMapping(value = "/file/{fileIndex:\\d+}/{spectrumIndex:\\d+}/search/", method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("fileIndex") final int fileIndex,
                               @PathVariable("spectrumIndex") final int spectrumIndex,
                               final HttpSession session, final Model model,
                               @Valid final FilterForm filterForm, final Errors errors) {

        final Submission submission = Submission.from(session);
        if (submission == null) {
            return new ModelAndView(new RedirectView("/file/upload/"));
        }

        final Spectrum spectrum = submission
                .getFiles()
                .get(fileIndex)
                .getSpectra()
                .get(spectrumIndex);

        return searchPost(spectrum, filterForm, model, errors);
    }

    @RequestMapping(value = "/spectrum/{spectrumId:\\d+}/search/", method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("spectrumId") final long spectrumId, final Model model,
                               @Valid final FilterForm filterForm, final Errors errors) {

        if (errors.hasErrors()) {
            return new ModelAndView("submission/spectrum/search");
        }

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);

        } catch (final EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView(new RedirectView("/notfound/"));
        }

        return searchPost(spectrum, filterForm, model, errors);
    }

    private ModelAndView searchPost(final Spectrum querySpectrum,
                                    final FilterForm filterForm,
                                    @Valid final Model model, final Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("querySpectrum", querySpectrum);
            return new ModelAndView("submission/spectrum/search");
        }

        SearchParameters parameters = SearchParameters.getDefaultParameters(querySpectrum.getChromatographyType());
        parameters.setSpecies(filterForm.getSpecies());
        parameters.setSource(filterForm.getSource());
        parameters.setDisease(filterForm.getDisease());
        parameters.setSubmissionIds(filterForm.getSubmissionIds());

        List<SearchResultDTO> searchResults = individualSearchService.searchConsensusSpectra(
                this.getCurrentUserPrincipal(), querySpectrum, parameters);

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("filterOptions", getFilterOptions(querySpectrum.getChromatographyType()));
        model.addAttribute("filterForm", filterForm);
        model.addAttribute("searchResults", searchResults);

        return new ModelAndView("submission/spectrum/search");
    }

    private FilterOptions getFilterOptions(ChromatographyType chromatographyType) {
        List<String> speciesList = submissionTagService.findDistinctTagValuesByTagKey("species (common)");
        List<String> sourceList = submissionTagService.findDistinctTagValuesByTagKey("sample source");
        List<String> diseaseList = submissionTagService.findDistinctTagValuesByTagKey("disease");

        SortedMap<Long, String> submissions = submissionService.findUserPrivateSubmissions(
                this.getCurrentUserPrincipal(), chromatographyType);
        submissions.put(0L, "Public");

        return new FilterOptions(speciesList, sourceList, diseaseList, submissions);
    }
}
