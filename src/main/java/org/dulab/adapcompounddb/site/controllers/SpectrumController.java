package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class SpectrumController {

    private final SpectrumService spectrumService;
    private final SubmissionService submissionService;

    @Autowired
    public SpectrumController(SpectrumService spectrumService, SubmissionService submissionService) {
        this.spectrumService = spectrumService;
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/spectrum/{spectrumId:\\d+}/", method = RequestMethod.GET)
    public String spectrum(@PathVariable("spectrumId") long spectrumId,
                           Model model) {

        Spectrum spectrum = spectrumService.find(spectrumId);

        if (spectrum == null)
            return spectrumNotFound(model,spectrumId);

        return spectrum(spectrum, model);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/{spectrumListIndex:\\d+}/", method = RequestMethod.GET)
    public String spectrum(@PathVariable("submissionId") long submissionId,
                           @PathVariable("spectrumListIndex") int spectrumListIndex,
                           Model model) {

        Submission submission = submissionService.findSubmission(submissionId);
        if (submission == null)
            return submissionNotFound(model,submissionId);

        Spectrum spectrum = submission.getSpectra().get(spectrumListIndex);

        return spectrum(spectrum, model);
    }

    @RequestMapping(value = "/file/{spectrumListIndex:\\d+}/", method = RequestMethod.GET)
    public String spectrum(@PathVariable("spectrumListIndex") int listIndex,
                           HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        if (submission == null)
           return submissionNotFound(model);
        Spectrum spectrum = submission.getSpectra().get(listIndex);

        return spectrum(spectrum, model);
    }

    public String spectrum(Spectrum spectrum, Model model) {
        model.addAttribute("spectrum", spectrum);
        return "file/spectrum";
    }

    private String spectrumNotFound(Model model, long spectrumId) {
        model.addAttribute("errorMessage", "Cannot find spectrum ID = " + spectrumId);
        return "redirect:/notfound/";
    }

    private String submissionNotFound(Model model, long submissionId) {
        model.addAttribute("errorMessage", "Cannot find submission ID = " + submissionId);
        return "redirect:/notfound/";
    }

    private String submissionNotFound(Model model) {
        model.addAttribute("errorMessage", "Cannot find submission");
        return "redirect:/notfound/";
    }
}
