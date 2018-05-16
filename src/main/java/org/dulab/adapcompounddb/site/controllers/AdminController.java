package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.StatisticsService;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.Statistics;
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
        spectrumMatchService.fillSpectrumMatchTable(0.01F, 0.75F);
        return "redirect:/admin/";
    }

    @RequestMapping(value = "/admin/cluster/", method = RequestMethod.GET)
    public String cluster() {
        try {
            spectrumMatchService.cluster(0.1F, 2, 0.75F);
        }
        catch (EmptySearchResultException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/admin/";
    }
}
