package org.dulab.adapcompounddb.site.controllers;

import javax.servlet.http.HttpSession;

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

@Controller
public class SpectrumController extends BaseController {

    private final SpectrumService spectrumService;
    private final SubmissionService submissionService;

    @Autowired
    public SpectrumController(final SpectrumService spectrumService, final SubmissionService submissionService) {
        this.spectrumService = spectrumService;
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/spectrum/{spectrumId:\\d+}", method = RequestMethod.GET)
    public String spectrum(@PathVariable("spectrumId") final long spectrumId, final Model model) {

        final Spectrum spectrum = spectrumService.find(spectrumId);

        return spectrum(spectrum, model);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/spectrum/{spectrumId:\\d+}")
    public String spectrum(@PathVariable("spectrumId") final int spectrumId, final Model model) {

        final Spectrum spectrum = spectrumService.find(spectrumId);

        return spectrum(spectrum, model);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/{fileIndex:\\d+}/{spectrumIndex:\\d+}/", method = RequestMethod.GET)
    public String spectrum(@PathVariable("submissionId") final long submissionId,
            @PathVariable("fileIndex") final int fileIndex, @PathVariable("spectrumIndex") final int spectrumIndex,
            final Model model) {

        final Submission submission = submissionService.findSubmission(submissionId);
        final Spectrum spectrum = submission.getFiles().get(fileIndex).getSpectra().get(spectrumIndex);

        return spectrum(spectrum, model);
    }

    @RequestMapping(value = "/file/{fileIndex:\\d+}/{spectrumIndex:\\d+}/", method = RequestMethod.GET)
    public String spectrum(@PathVariable("fileIndex") final int fileIndex,
            @PathVariable("spectrumIndex") final int spectrumIndex, final HttpSession session, final Model model) {

        final Submission submission = getSubmissionFromSession(session);
        final Spectrum spectrum = submission.getFiles().get(fileIndex).getSpectra().get(spectrumIndex);

        return spectrum(spectrum, model);
    }

    public String spectrum(final Spectrum spectrum, final Model model) {
        model.addAttribute("spectrum", spectrum);
        return "file/spectrum";
    }
}
