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
import org.dulab.adapcompounddb.site.controllers.utils.ConversionsUtils;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.SubmissionTagService;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Future;

import static org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils.META_FIELDS_COOKIE_NAME;
import static org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils.SEARCH_PARAMETERS_COOKIE_NAME;

@Controller
public class GroupSearchController extends BaseController {

    private static final Logger LOGGER = LogManager.getLogger(GroupSearchController.class);

    private static final String ALL = "all";

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

    @RequestMapping(value = "/group_search/parameters", method = RequestMethod.GET)
    public String groupSearchParametersGet(@RequestParam Optional<Boolean> withOntologyLevels,
                                           @RequestParam Optional<Long> submissionId,
                                           HttpSession session, Model model,
                                           @CookieValue(
                                                   value = SEARCH_PARAMETERS_COOKIE_NAME,
                                                   defaultValue = "") String searchParametersCookie) {

//        session.removeAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME);

        Submission submission = submissionId
                .map(submissionService::findSubmission)
                .orElseGet(() -> Submission.from(session));

        if (submission == null)
            return "redirect:/file/upload/";

        FilterOptions filterOptions = getFilterOptions(getChromatographyTypes(submission));
        model.addAttribute("filterOptions", filterOptions);

        FilterForm form = ConversionsUtils.byteStringToForm(searchParametersCookie, FilterForm.class);
        if (form.getSubmissionIds() == null || form.getSubmissionIds().isEmpty())
            form.setSubmissionIds(filterOptions.getSubmissions().keySet());
        form.setWithOntologyLevels(withOntologyLevels.orElse(false));
        model.addAttribute("filterForm", form);
        return "submission/group_search_parameters";
    }

    @RequestMapping(value = "/group_search/parameters", method = RequestMethod.POST)
    public String groupSearchParametersPost(@RequestParam Optional<Long> submissionId, HttpSession session, Model model,
                                            HttpServletResponse response, @Valid FilterForm form, Errors errors) {

        Submission submission = submissionId
                .map(submissionService::findSubmission)
                .orElseGet(() -> Submission.from(session));

        FilterOptions filterOptions = getFilterOptions(getChromatographyTypes(submission));
        model.addAttribute("filterOptions", filterOptions);

        if (errors.hasErrors()) {
            return "submission/group_search_parameters";
        }

        if (asyncResult != null && !asyncResult.isDone()) {
            asyncResult.cancel(true);
        }

        String species = ALL.equalsIgnoreCase(form.getSpecies()) ? null : form.getSpecies();
        String source = ALL.equalsIgnoreCase(form.getSource()) ? null : form.getSource();
        String disease = ALL.equalsIgnoreCase(form.getDisease()) ? null : form.getDisease();

        SearchParameters parameters = new SearchParameters();
        parameters.setScoreThreshold(form.getScoreThreshold() != null ? form.getScoreThreshold() / 1000.0 : null);
        parameters.setRetIndexTolerance(
                form.getRetentionIndexTolerance() != null ? (double) form.getRetentionIndexTolerance() : null);
        parameters.setRetIndexMatchType(form.getRetentionIndexMatch());
        parameters.setMzTolerance(form.getMzTolerance(), form.getMzToleranceType());
        parameters.setLimit(form.getLimit());
        parameters.setSpecies(species);
        parameters.setSource(source);
        parameters.setDisease(disease);
        parameters.setSubmissionIds(form.getSubmissionIds());

        asyncResult = groupSearchService.groupSearch(this.getCurrentUserPrincipal(), submission.getFiles(), session,
                parameters, form.isWithOntologyLevels());

        String byteString = ConversionsUtils.formToByteString(form);
        Cookie metaFieldsCookie = new Cookie(SEARCH_PARAMETERS_COOKIE_NAME, byteString);
        response.addCookie(metaFieldsCookie);

        return "redirect:/group_search/";
    }

    @RequestMapping(value = "/group_search/", method = RequestMethod.GET)
    public String groupSearch() {
        return "submission/group_search";
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

        SortedMap<BigInteger, String> submissions = new TreeMap<>();
        for (ChromatographyType chromatographyType : chromatographyTypes) {
            submissions.putAll(
                    submissionService.findUserPrivateSubmissions(this.getCurrentUserPrincipal(), chromatographyType));
            submissions.putAll(submissionService.findPublicSubmissions(chromatographyType));
        }
        submissions.put(BigInteger.ZERO, "ADAP-KDB Consensus Spectra");

        return new FilterOptions(speciesList, sourceList, diseaseList, submissions);
    }
}
