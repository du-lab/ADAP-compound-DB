package org.dulab.site.controllers;

import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.*;
import org.dulab.models.search.ComparisonOperator;
import org.dulab.models.search.CriteriaBlock;
import org.dulab.models.search.Criterion;
import org.dulab.models.search.SetOperator;
import org.dulab.site.services.SpectrumService;
import org.dulab.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Controller
@ControllerAdvice
public class SearchController {

    private final SubmissionService submissionService;
    private final SpectrumService spectrumService;

    @Autowired
    public SearchController(SubmissionService submissionService, SpectrumService spectrumService) {
        this.submissionService = submissionService;
        this.spectrumService = spectrumService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("chromatographyTypes", ChromatographyType.values());
        model.addAttribute("submissionCategoryIds", submissionService.getAllSubmissionCategories());
    }

    @RequestMapping(
            value = "/submission/{submissionId:\\d+}/{spectrumListIndex:\\d+}/search/",
            method = RequestMethod.GET)
    public String search(@PathVariable("submissionId") long submissionId,
                         @PathVariable("spectrumListIndex") int spectrumListIndex,
                         Model model) {

        Spectrum spectrum;
        try {
            spectrum = getQuerySpectrum(submissionId, spectrumListIndex);

        } catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/notfound/";
        }

        return searchGet(spectrum, model);
    }

    @RequestMapping(value = "/file/{spectrumListIndex:\\d+}/search/", method = RequestMethod.GET)
    public String search(@PathVariable("spectrumListIndex") int spectrumListIndex,
                         HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        if (submission == null)
            return "redirect:/file/upload/";

        Spectrum spectrum = submission.getSpectra().get(spectrumListIndex);

        return searchGet(spectrum, model);
    }

    @RequestMapping(value = "/spectrum/{spectrumId}/search/", method = RequestMethod.GET)
    public String search(@PathVariable("spectrumId") long spectrumId, Model model) {

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);
        } catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/notfound/";
        }

        return searchGet(spectrum, model);
    }

    private String searchGet(Spectrum querySpectrum, Model model) {

        UserParameters params = new UserParameters();

        SearchForm form = new SearchForm();
        form.setMzTolerance(params.getSpectrumSearchMzTolerance());
        form.setNumHits(params.getSpectrumSearchNumHits());
        form.setScoreThreshold(
                Math.round(
                        SearchForm.THRESHOLD_FACTOR * params.getSpectrumSearchScoreThreshold()));
        form.setChromatographyTypeCheck(false);
        form.setSubmissionCategoryCheck(false);

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("form", form);

        return "file/match";
    }


    @RequestMapping(
            value = "/submission/{submissionId:\\d+}/{spectrumListIndex:\\d+}/search/",
            method = RequestMethod.POST)
    public String search(@PathVariable("submissionId") long submissionId,
                         @PathVariable("spectrumListIndex") int spectrumListIndex,
                         Model model, @Valid SearchForm form, Errors errors) {

        if (errors.hasErrors()) return "file/match";

        Spectrum spectrum;
        try {
            spectrum = getQuerySpectrum(submissionId, spectrumListIndex);

        } catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/notfound/";
        }

        return searchPost(spectrum, form, model);
    }

    @RequestMapping(value = "/file/{spectrumListIndex:\\d+}/search/", method = RequestMethod.POST)
    public String search(@PathVariable("spectrumListIndex") int spectrumListIndex,
                         HttpSession session, Model model, @Valid SearchForm form, Errors errors) {

        if (errors.hasErrors()) return "file/match";

        Submission submission = Submission.from(session);
        if (submission == null)
            return "redirect:/file/upload/";

        Spectrum spectrum = submission.getSpectra().get(spectrumListIndex);

        return searchPost(spectrum, form, model);
    }

    @RequestMapping(value = "/spectrum/{spectrumId:\\d+}/search/", method = RequestMethod.POST)
    public String search(@PathVariable("spectrumId") long spectrumId,
                         Model model, @Valid SearchForm form, Errors errors) {

        if (errors.hasErrors()) return "file/match";

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);

        } catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/notfound/";
        }

        return searchPost(spectrum, form, model);
    }

    private String searchPost(Spectrum querySpectrum, SearchForm form, Model model) {

        UserParameters params = new UserParameters();

        params.setSpectrumSearchMzTolerance(form.getMzTolerance());
        params.setSpectrumSearchNumHits(form.getNumHits());
        params.setSpectrumSearchScoreThreshold(form.getScoreThreshold() / SearchForm.THRESHOLD_FACTOR);

        CriteriaBlock criteria = new CriteriaBlock(SetOperator.AND);
        if (form.isChromatographyTypeCheck())
            criteria.add(
                    new Criterion("ChromatographyType", ComparisonOperator.EQ, form.chromatographyType));
        if (form.isSubmissionCategoryCheck()) {
            CriteriaBlock categories = new CriteriaBlock(SetOperator.OR);
            for (long id : form.getSubmissionCategoryIds())
                categories.add(
                        new Criterion("SubmissionCategoryId", ComparisonOperator.EQ, id));
            criteria.add(new Criterion("", ComparisonOperator.BLOCK, categories));
        }

        try {
            List<Hit> hits = spectrumService.match(querySpectrum, criteria, params);
            model.addAttribute("hits", hits);

        } catch (EmptySearchResultException e) {
            model.addAttribute("searchResultMessage", e.getMessage());
        }

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("form", form);

        return "file/match";
    }


    private Spectrum getQuerySpectrum(long submissionId, int spectrumListIndex) {
        return submissionService.findSubmission(submissionId)
                .getSpectra()
                .get(spectrumListIndex);
    }


    public static class SearchForm {

        static final float THRESHOLD_FACTOR = 1000;

        @Min(value = 0, message = "M/z tolerance must be positive.")
        private float mzTolerance;

        @Min(value = 1, message = "Maximum number of hits must be greater than or equal to one.")
        private int numHits;

        @Min(value = 0, message = "Matching score threshold must be between 0 and 1000.")
        @Max(value = 1000, message = "Matching score threshold must be between 0 and 1000.")
        private int scoreThreshold;

        private boolean chromatographyTypeCheck;

        private ChromatographyType chromatographyType;

        private boolean submissionCategoryCheck;

        private List<Long> submissionCategoryIds;

        public float getMzTolerance() {
            return mzTolerance;
        }

        public void setMzTolerance(float mzTolerance) {
            this.mzTolerance = mzTolerance;
        }

        public int getNumHits() {
            return numHits;
        }

        public void setNumHits(int numHits) {
            this.numHits = numHits;
        }

        public int getScoreThreshold() {
            return scoreThreshold;
        }

        public void setScoreThreshold(int scoreThreshold) {
            this.scoreThreshold = scoreThreshold;
        }

        public boolean isChromatographyTypeCheck() {
            return chromatographyTypeCheck;
        }

        public void setChromatographyTypeCheck(boolean chromatographyTypeCheck) {
            this.chromatographyTypeCheck = chromatographyTypeCheck;
        }

        public ChromatographyType getChromatographyType() {
            return chromatographyType;
        }

        public void setChromatographyType(ChromatographyType chromatographyType) {
            this.chromatographyType = chromatographyType;
        }

        public boolean isSubmissionCategoryCheck() {
            return submissionCategoryCheck;
        }

        public void setSubmissionCategoryCheck(boolean submissionCategoryCheck) {
            this.submissionCategoryCheck = submissionCategoryCheck;
        }

        public List<Long> getSubmissionCategoryIds() {
            return submissionCategoryIds;
        }

        public void setSubmissionCategoryIds(List<Long> submissionCategoryIds) {
            this.submissionCategoryIds = submissionCategoryIds;
        }

        void toUserParameters(UserParameters ups) {
            ups.setSpectrumSearchMzTolerance(mzTolerance);
            ups.setSpectrumSearchNumHits(numHits);
            ups.setSpectrumSearchScoreThreshold(scoreThreshold / 1000.0F);
        }
    }
}
