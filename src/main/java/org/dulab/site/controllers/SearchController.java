package org.dulab.site.controllers;

import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.ChromatographyType;
import org.dulab.models.Spectrum;
import org.dulab.models.Submission;
import org.dulab.models.UserParameters;
import org.dulab.site.services.SpectrumService;
import org.dulab.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

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

    @RequestMapping(value = "/submission/{submissionId:\\d+}/{spectrumListIndex:\\d+}/search/", method = RequestMethod.GET)
    public String search(@PathVariable("submissionId") long submissionId,
                         @PathVariable("spectrumListIndex") int spectrumListIndex,
                         Model model) {

        Spectrum spectrum;

        try {
            Submission submission = submissionService.findSubmission(submissionId);
            spectrum = submission.getSpectra().get(spectrumListIndex);
        }
        catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/notfound/";
        }

        return search(spectrum, model);
    }

    @RequestMapping(value = "/submission/0/{spectrumListIndex:\\d+}/search/", method = RequestMethod.GET)
    public String search(@PathVariable("spectrumListIndex") int spectrumListIndex,
                         HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        if (submission == null)
            return "redirect:/file/upload/";

        Spectrum spectrum = submission.getSpectra().get(spectrumListIndex);

        return search(spectrum, model);
    }

    @RequestMapping(value = "/spectrum/{spectrumId}/search/", method = RequestMethod.GET)
    public String search(@PathVariable("spectrumId") long spectrumId, Model model) {

        Spectrum spectrum;
        try {
            spectrum = spectrumService.find(spectrumId);
        }
        catch (EmptySearchResultException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/notfound";
        }

        return search(spectrum, model);
    }

    public String search(Spectrum querySpectrum, Model model) {

        UserParameters userParameters = new UserParameters();

        SubmissionController.SpectrumSearchForm form = new SubmissionController.SpectrumSearchForm();
        form.fromUserParameters(userParameters);
        form.setChromatographyTypeCheck(false);
        form.setSubmissionCategoryCheck(false);

        model.addAttribute("querySpectrum", querySpectrum);
        model.addAttribute("form", form);
        model.addAttribute("chromatographyTypes", ChromatographyType.values());

        return "file/match";
    }
}
