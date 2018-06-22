package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.Hit;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.search.ComparisonOperator;
import org.dulab.adapcompounddb.models.search.CriteriaBlock;
import org.dulab.adapcompounddb.models.search.Criterion;
import org.dulab.adapcompounddb.models.search.SetOperator;
import org.dulab.adapcompounddb.site.services.SpectrumSearchService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.validation.FloatMin;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.hibernate.validator.constraints.Range;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ControllerAdvice
public class SearchController {

    static final String MZ_TOLERANCE_KEY = "spectrum_search_mz_tolerance";
    static final String NUM_HITS_KEY = "spectrum_search_num_hits";
    static final String SCORE_THRESHOLD_KEY = "spectrum_search_score_threshold";
    static final String CHROMATOGRAPHY_TYPE_CHECK_KEY = "spectrum_search_chromatography_type_check";
    static final String CHROMATOGRAPHY_TYPE_KEY = "spectrum_search_chromatography_type";
    static final String SUBMISSION_CATEGORY_IDS_CHECK_KEY = "spectrum_search_submission_category_ids_check";
    static final String SUBMISSION_CATEGORY_IDS_KEY = "spectrum_search_submission_category_ids";

    private final UserPrincipalService userPrincipalService;
    private final SubmissionService submissionService;
    private final SpectrumService spectrumService;

    private final Map<ChromatographyType, SpectrumSearchService> spectrumSearchServiceMap;

    @Autowired
    public SearchController(UserPrincipalService userPrincipalService,
                            SubmissionService submissionService,
                            @Qualifier("spectrumServiceImpl") SpectrumService spectrumService,
                            @Qualifier("GCSpectrumSearchServiceImpl") SpectrumSearchService gcSpectrumSearchService,
                            @Qualifier("LCSpectrumSearchServiceImpl") SpectrumSearchService lcSpectrumSearchService) {

        this.userPrincipalService = userPrincipalService;
        this.submissionService = submissionService;
        this.spectrumService = spectrumService;

        this.spectrumSearchServiceMap = new HashMap<>();
        this.spectrumSearchServiceMap.put(ChromatographyType.GAS, gcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_POSITIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_NEGATIVE, lcSpectrumSearchService);
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("chromatographyTypes", ChromatographyType.values());
        model.addAttribute("submissionSources", submissionService.getAllSources());
        model.addAttribute("submissionSpecies", submissionService.getAllSpecies());
        model.addAttribute("submissionDiseases", submissionService.getAllDiseases());
    }

//    @PostConstruct
//    public void init() {
//        userPrincipalService.saveDefaultParameter(MZ_TOLERANCE_KEY, UserParameterType.FLOAT, 0.1F);
//        userPrincipalService.saveDefaultParameter(NUM_HITS_KEY, UserParameterType.INTEGER, 10);
//        userPrincipalService.saveDefaultParameter(SCORE_THRESHOLD_KEY, UserParameterType.FLOAT, 0.75F);
//        userPrincipalService.saveDefaultParameter(CHROMATOGRAPHY_TYPE_CHECK_KEY, UserParameterType.BOOLEAN, false);
//        userPrincipalService.saveDefaultParameter(CHROMATOGRAPHY_TYPE_KEY, UserParameterType.CHROMATOGRAPHY_TYPE, ChromatographyType.GAS);
//        userPrincipalService.saveDefaultParameter(SUBMISSION_CATEGORY_IDS_CHECK_KEY, UserParameterType.BOOLEAN, false);
//        userPrincipalService.saveDefaultParameter(SUBMISSION_CATEGORY_IDS_KEY, UserParameterType.INTEGER_LIST, new ArrayList<>(0));
//    }

    @RequestMapping(
            value = "/submission/{submissionId:\\d+}/{spectrumListIndex:\\d+}/search/",
            method = RequestMethod.GET)
    public String search(@PathVariable("submissionId") long submissionId,
                         @PathVariable("spectrumListIndex") int spectrumListIndex,
                         HttpSession session, Model model) {

        Spectrum spectrum;
        try {
            spectrum = getQuerySpectrum(submissionId, spectrumListIndex);

        } catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/notfound/";
        }

        return searchGet(spectrum, UserPrincipal.from(session), model);
    }

    @RequestMapping(value = "/file/{spectrumListIndex:\\d+}/search/", method = RequestMethod.GET)
    public String search(@PathVariable("spectrumListIndex") int spectrumListIndex,
                         HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        if (submission == null)
            return "redirect:/file/upload/";

        Spectrum spectrum = submission.getSpectra().get(spectrumListIndex);

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
//        form.initialize(userPrincipalService, user);

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("searchForm", form);

        return "file/match";
    }

    @RequestMapping(
            value = "/submission/{submissionId:\\d+}/{spectrumListIndex:\\d+}/search/",
            method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("submissionId") long submissionId,
                               @PathVariable("spectrumListIndex") int spectrumListIndex,
                               HttpSession session, Model model, @Valid SearchForm searchForm, Errors errors) {

        Spectrum spectrum;
        try {
            spectrum = getQuerySpectrum(submissionId, spectrumListIndex);

        } catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return new ModelAndView(new RedirectView("/notfound/"));
        }

        return searchPost(spectrum, UserPrincipal.from(session), searchForm, model, errors);
    }

    @RequestMapping(value = "/file/{spectrumListIndex:\\d+}/search/", method = RequestMethod.POST)
    public ModelAndView search(@PathVariable("spectrumListIndex") int spectrumListIndex,
                         HttpSession session, Model model, @Valid SearchForm form, Errors errors) {

        Submission submission = Submission.from(session);
        if (submission == null)
            return new ModelAndView(new RedirectView("/file/upload/"));

        Spectrum spectrum = submission.getSpectra().get(spectrumListIndex);

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




//        CriteriaBlock criteria = new CriteriaBlock(SetOperator.AND);
//        if (form.isChromatographyTypeCheck())
//            criteria.add(
//                    new Criterion("ChromatographyType", ComparisonOperator.EQ, form.getChromatographyType()));
//        if (form.isSubmissionCategoryCheck()) {
//            CriteriaBlock categories = new CriteriaBlock(SetOperator.OR);
//            for (long id : form.getSubmissionCategoryIds())
//                categories.add(
//                        new Criterion("SubmissionCategoryId", ComparisonOperator.EQ, id));
//            criteria.add(new Criterion("", ComparisonOperator.BLOCK, categories));
//        }

//        try {
//            List<Hit> hits = spectrumService.match(querySpectrum, criteria,
//                    form.getMzTolerance(), form.getNumHits(), form.getFloatScoreThreshold());
//            model.addAttribute("hits", hits);
//
////            form.saveParameters(userPrincipalService, user);
//
//        } catch (EmptySearchResultException e) {
//            model.addAttribute("searchResultMessage", e.getMessage());
//        }

        QueryParameters parameters = new QueryParameters();
        parameters.setMzTolerance(form.getMzTolerance());
        parameters.setScoreThreshold(form.getFloatScoreThreshold());
        List<SpectrumMatch> matches = service.search(querySpectrum, parameters);

        model.addAttribute("matches", matches);

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("form", form);

        return new ModelAndView("file/match");
    }


    private Spectrum getQuerySpectrum(long submissionId, int spectrumListIndex) {
        return submissionService.findSubmission(submissionId)
                .getSpectra()
                .get(spectrumListIndex);
    }

    public static class SearchForm {

        private boolean scoreThresholdCheck = true;

        @Min(value = 0, message = "M/z tolerance must be positive.")
        private float mzTolerance = 0.01F;

        @Min(value = 0, message = "Matching score threshold must be between 0 and 1000.")
        @Max(value = 1000, message = "Matching score threshold must be between 0 and 1000.")
        private int scoreThreshold;

        private boolean massToleranceCheck = true;

        @Min(value = 0, message = "M/z tolerance must be positive.")
        private float massTolerance = 0.01F;

        private boolean retTimeToleranceCheck = false;

        private float retTimeTolerance = 0.5F;

        public float getMzTolerance() {
            return mzTolerance;
        }

        public void setMzTolerance(float mzTolerance) {
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

        public float getFloatScoreThreshold() {
            return scoreThreshold / 1000.0F;
        }

        public boolean isMassToleranceCheck() {
            return massToleranceCheck;
        }

        public void setMassToleranceCheck(boolean massToleranceCheck) {
            this.massToleranceCheck = massToleranceCheck;
        }

        public float getMassTolerance() {
            return massTolerance;
        }

        public void setMassTolerance(float massTolerance) {
            this.massTolerance = massTolerance;
        }

        public boolean isRetTimeToleranceCheck() {
            return retTimeToleranceCheck;
        }

        public void setRetTimeToleranceCheck(boolean retTimeToleranceCheck) {
            this.retTimeToleranceCheck = retTimeToleranceCheck;
        }

        public float getRetTimeTolerance() {
            return retTimeTolerance;
        }

        public void setRetTimeTolerance(float retTimeTolerance) {
            this.retTimeTolerance = retTimeTolerance;
        }
    }
}
