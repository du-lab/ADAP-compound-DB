package org.dulab.site.controllers;

import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.site.services.SpectrumMatchService;
import org.dulab.site.services.SpectrumService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@ControllerAdvice
public class AdminController {

    private final SpectrumMatchService spectrumMatchService;
    private final SpectrumService spectrumService;

    public AdminController(SpectrumMatchService spectrumMatchService, SpectrumService spectrumService) {
        this.spectrumMatchService = spectrumMatchService;
        this.spectrumService = spectrumService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("numSpectra", spectrumService.getTotalNumberOfSpectra());
        model.addAttribute("numClusters", spectrumMatchService.getTotalNumberOfClusters());
        model.addAttribute("clusters", spectrumMatchService.getAllClusters());
    }

    @RequestMapping(value = "/admin/", method = RequestMethod.GET)
    public String admin() {
        return "admin/view";
    }

    @RequestMapping(value = "/admin/calculatescores/", method = RequestMethod.GET)
    public String calculateScores() {
        spectrumMatchService.fillSpectrumMatchTable();
        return "redirect:/admin/";
    }

    @RequestMapping(value = "/admin/cluster/", method = RequestMethod.GET)
    public String cluster() {
        try {
            spectrumMatchService.cluster(0.25F, 2);
        }
        catch (EmptySearchResultException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/admin/";
    }
}
