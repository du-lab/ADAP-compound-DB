package org.dulab.site.controllers;

import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.ChromatographyType;
import org.dulab.models.Statistics;
import org.dulab.site.services.StatisticsService;
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
    private final StatisticsService statisticsService;

    public AdminController(SpectrumMatchService spectrumMatchService,
                           StatisticsService statisticsService) {

        this.spectrumMatchService = spectrumMatchService;
        this.statisticsService = statisticsService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {

        Map<ChromatographyType, Statistics> statisticsMap = new TreeMap<>();
        for (ChromatographyType type : ChromatographyType.values())
            statisticsMap.put(type, statisticsService.getStatistics(type));

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
            spectrumMatchService.cluster(0.1F, 2, 0.5F);
        }
        catch (EmptySearchResultException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/admin/";
    }
}
