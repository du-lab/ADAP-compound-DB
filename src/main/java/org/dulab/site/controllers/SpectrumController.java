package org.dulab.site.controllers;

import org.dulab.models.Spectrum;
import org.dulab.models.Submission;
import org.dulab.site.services.SpectrumService;
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

    @Autowired
    public SpectrumController(SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }

    @RequestMapping(value = "spectrum/{spectrumId:\\d+}/", method = RequestMethod.GET)
    public String spectrum(@PathVariable("spectrumId") long spectrumId,
                           Model model) {

        Spectrum spectrum = spectrumService
                .find(spectrumId)
                .orElseThrow(() -> new IllegalStateException("Cannot find spectrum with ID = " + spectrumId));

        return spectrum(spectrum, model);
    }

    @RequestMapping(value = "file/{spectrumListIndex:\\d+}/", method = RequestMethod.GET)
    public String spectrum(@PathVariable("spectrumListIndex") int listIndex,
                           HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        Spectrum spectrum = submission.getSpectra().get(listIndex);

        return spectrum(spectrum, model);
    }

    public String spectrum(Spectrum spectrum, Model model) {
        model.addAttribute("spectrum", spectrum);
        return "file/spectrum";
    }
}
