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

    @RequestMapping(value = "/spectrum/{spectrumId:\\d+}", method = RequestMethod.GET)
    public String spectrum(@PathVariable("spectrumId") long spectrumId,
                           Model model) {

        Spectrum spectrum = spectrumService.find(spectrumId);

        return spectrum(spectrum, model);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/spectrum/{spectrumId:\\d+}")
    public String spectrum(@PathVariable("spectrumId") int spectrumId,
                           Model model) {

        Spectrum spectrum = spectrumService.find(spectrumId);

        return spectrum(spectrum, model);
    }

    @RequestMapping(value = "/file/{fileIndex:\\d+}/{spectrumIndex:\\d+}/", method = RequestMethod.GET)
    public String spectrum(@PathVariable("fileIndex") int fileIndex,
                           @PathVariable("spectrumIndex") int spectrumIndex,
                           HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        Spectrum spectrum = submission
                .getFiles()
                .get(fileIndex)
                .getSpectra()
                .get(spectrumIndex);

        return spectrum(spectrum, model);
    }

    public String spectrum(Spectrum spectrum, Model model) {
        model.addAttribute("spectrum", spectrum);
        return "file/spectrum";
    }
}
