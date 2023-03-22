package org.dulab.adapcompounddb.site.controllers;

import java.util.stream.Collectors;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.dto.SearchParametersDTO;
import org.dulab.adapcompounddb.models.entities.SearchTask;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.SearchTaskStatus;
import org.dulab.adapcompounddb.site.services.SearchTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.controllers.forms.FilterForm;
import org.dulab.adapcompounddb.site.controllers.forms.FilterOptions;
import org.dulab.adapcompounddb.site.controllers.utils.ConversionsUtils;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.SubmissionTagService;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.*;

import static org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils.*;

@Controller
public class GroupSearchController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupSearchController.class);

    private static final String ALL = "all";

    private final GroupSearchService groupSearchService;
    private final SubmissionService submissionService;
    private final SubmissionTagService submissionTagService;
    private final SearchTaskService searchTaskService;
    private FilterOptions filterOptions;

    @Autowired
    public GroupSearchController(GroupSearchService groupSearchService,
                                 SubmissionService submissionService,
                                 SubmissionTagService submissionTagService,
                                 SearchTaskService searchTaskService) {

        this.groupSearchService = groupSearchService;
        this.submissionService = submissionService;
        this.submissionTagService = submissionTagService;
        this.searchTaskService = searchTaskService;
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

        FilterOptions filterOptions = getFilterOptions(Collections.singletonList(submission.getChromatographyType()));
        model.addAttribute("filterOptions", filterOptions);

        FilterForm form = ConversionsUtils.byteStringToForm(searchParametersCookie, FilterForm.class);
        if (form.getSubmissionIds() == null || form.getSubmissionIds().isEmpty())
            form.setSubmissionIds(filterOptions.getSubmissions().keySet());
        form.setWithOntologyLevels(withOntologyLevels.orElse(false));
        model.addAttribute("filterForm", form);

        //check if user is login
        model.addAttribute("isLoggedIn", this.getCurrentUserPrincipal() != null);
        model.addAttribute("searchParameters", this.getCurrentUserPrincipal() != null ?
        this.getCurrentUserPrincipal().getSearchParametersDTO() : new SearchParametersDTO());
        return "submission/group_search_parameters";
    }


    @RequestMapping(value = "/group_search/parameters", method = RequestMethod.POST)
    public String groupSearchParametersPost(@RequestParam Optional<Long> submissionId, HttpSession session, Model model,
                                             HttpServletRequest request, HttpServletResponse response,
                                             @Valid FilterForm form, Errors errors,
                                             RedirectAttributes redirectAttributes) throws TimeoutException {

        Submission submission = submissionId
                .map(submissionService::fetchSubmission)
                .orElseGet(() -> Submission.from(session));

        boolean savedSubmission =  (submission.getId() != 0)? true : false;


        FilterOptions filterOptions = getFilterOptions(Collections.singletonList(submission.getChromatographyType()));
        model.addAttribute("filterOptions", filterOptions);
        session.removeAttribute(GROUP_SEARCH_ERROR_ATTRIBUTE_NAME);
        if (errors.hasErrors()) {
            return "submission/group_search_parameters";
        }

        @SuppressWarnings("unchecked")
        Future<Void> asyncResult = (Future<Void>) session.getAttribute(GROUP_SEARCH_ASYNC_ATTRIBUTE_NAME);

        if (asyncResult != null && !asyncResult.isDone()) {
            asyncResult.cancel(true);
            session.removeAttribute(GROUP_SEARCH_ASYNC_ATTRIBUTE_NAME);
        }

        String species = ALL.equalsIgnoreCase(form.getSpecies()) ? null : form.getSpecies();
        String source = ALL.equalsIgnoreCase(form.getSource()) ? null : form.getSource();
        String disease = ALL.equalsIgnoreCase(form.getDisease()) ? null : form.getDisease();

        SearchParameters parameters = new SearchParameters();
        parameters.setScoreThreshold(form.getScoreThreshold() != null ? form.getScoreThreshold() / 1000.0 : null);
        parameters.setRetTimeTolerance(form.getRetentionTimeTolerance());
        parameters.setRetIndexTolerance(
                form.getRetentionIndexTolerance() != null ? (double) form.getRetentionIndexTolerance() : null);
        parameters.setRetIndexMatchType(form.getRetentionIndexMatch());
        parameters.setMzTolerance(form.getMzTolerance(), form.getMzToleranceType());
        parameters.setLimit(form.getLimit());
        parameters.setSpecies(species);
        parameters.setSource(source);
        parameters.setDisease(disease);
        parameters.setSubmissionIds(form.getSubmissionIds());

        Map<BigInteger, String> filteredLibraries = new TreeMap<>();
        for(Map.Entry<BigInteger, String> entry : filterOptions.getSubmissions().entrySet()){
            if(form.getSubmissionIds().contains(entry.getKey())){
                filteredLibraries.put(entry.getKey(), entry.getValue());
            }
        }


        asyncResult = groupSearchService.groupSearch(this.getCurrentUserPrincipal(), submission, submission.getFiles(), session,
                parameters, filteredLibraries, form.isWithOntologyLevels(), form.isSendResultsToEmail(), savedSubmission);
        session.setAttribute(GROUP_SEARCH_ASYNC_ATTRIBUTE_NAME, asyncResult);


        if(!asyncResult.isDone()){
            //update search task status to PENDING
            UserPrincipal user = this.getCurrentUserPrincipal();
            if(user != null) {
                //TODO: put this in a function?
                SearchTask searchTask;
                Optional<SearchTask> retreivedSearchTask = searchTaskService.findByUserIdAndSubmissionId(user.getId(), submissionId.get());
                if(retreivedSearchTask.isPresent())
                    searchTask = retreivedSearchTask.get();
                else{
                    searchTask = new SearchTask();
                    searchTask.setSubmission(submission);
                    searchTask.setUser(user);
                }

                searchTask.setLibraries(filteredLibraries);
                searchTask.setDateTime(new Date());
                searchTask.setStatus(SearchTaskStatus.PENDING);
                SearchTask savedSearchTask = searchTaskService.save(searchTask);
                if (savedSearchTask == null) {
                    LOGGER.warn("Could not update search task with user id: " + user.getId() + "and submission id: "
                        + submissionId);
                }
            }
        }
        LOGGER.info(String.format("Group search is started by user %s with IP = %s [%s]",
                this.getCurrentUserPrincipal(), request.getRemoteAddr(), request.getHeader("X-Forwarded-For")));

        String byteString = ConversionsUtils.formToByteString(form);
        Cookie metaFieldsCookie = new Cookie(SEARCH_PARAMETERS_COOKIE_NAME, byteString);
        response.addCookie(metaFieldsCookie);


        return "redirect:/group_search/";
    }

    @RequestMapping(value = "/group_search/", method = RequestMethod.GET)
    public String groupSearch(Model model, HttpSession session) {


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
