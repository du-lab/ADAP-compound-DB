package org.dulab.adapcompounddb.rest.controllers;

import org.dulab.adapcompounddb.site.services.SpectrumMatchCalculator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

    private final SpectrumMatchCalculator spectrumMatchCalculator;

    public AdminRestController(SpectrumMatchCalculator spectrumMatchCalculator) {
        this.spectrumMatchCalculator = spectrumMatchCalculator;
    }

    @RequestMapping(value = "/calculatescores/progress", produces="application/json")
    public int calculateScoresProgress() {
        return Math.round(100 * spectrumMatchCalculator.getProgress());
    }
}
