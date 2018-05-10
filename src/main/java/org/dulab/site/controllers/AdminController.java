package org.dulab.site.controllers;

import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.ChromatographyType;
import org.dulab.models.DatabaseStatistics;
import org.dulab.site.services.SpectrumMatchService;
import org.dulab.site.services.SpectrumService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;
import java.util.TreeMap;

@Controller
public class AdminController {

    private final SpectrumMatchService spectrumMatchService;
    private final SpectrumService spectrumService;

    public AdminController(SpectrumMatchService spectrumMatchService, SpectrumService spectrumService) {
        this.spectrumMatchService = spectrumMatchService;
        this.spectrumService = spectrumService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {

        Map<ChromatographyType, DatabaseStatistics> statisticsMap = new TreeMap<>();
        for (ChromatographyType chromatographyType : ChromatographyType.values())
            statisticsMap.put(chromatographyType, spectrumService.getStatistics(chromatographyType));

        model.addAttribute("statistics", statisticsMap);
        model.addAttribute("clusters", spectrumMatchService.getAllClusters());
    }

    @RequestMapping(value = "/admin/", method = RequestMethod.GET)
    public String admin() {
        return "admin/view";
    }

    @RequestMapping(value = "/admin/calculatescores/", method = RequestMethod.GET)
    public String calculateScores() {
        spectrumMatchService.fillSpectrumMatchTable(0.1F, 0.75F);
        return "redirect:/admin/";
    }

    @RequestMapping(value = "/admin/cluster/", method = RequestMethod.GET)
    public String cluster() {
        try {
            spectrumMatchService.cluster(0.1F, 2, 0.25F);
        }
        catch (EmptySearchResultException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/admin/";
    }
}
