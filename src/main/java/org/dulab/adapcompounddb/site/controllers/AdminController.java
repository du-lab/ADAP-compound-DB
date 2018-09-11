package org.dulab.adapcompounddb.site.controllers;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.Statistics;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.UserRole;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.site.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminController {

    private final SpectrumMatchCalculator spectrumMatchCalculator;
    private final SpectrumClusterer spectrumClusterer;
    private final SpectrumMatchService spectrumMatchService;
    private final StatisticsService statisticsService;
    private final UserPrincipalService userPrincipalService;

    private final Progress progress;

    @Autowired
    public AdminController(SpectrumMatchCalculator spectrumMatchCalculator,
                           SpectrumClusterer spectrumClusterer,
                           SpectrumMatchService spectrumMatchService,
                           StatisticsService statisticsService,
                           UserPrincipalService userPrincipalService) {

        this.spectrumMatchCalculator = spectrumMatchCalculator;
        this.spectrumClusterer = spectrumClusterer;
        this.spectrumMatchService = spectrumMatchService;
        this.statisticsService = statisticsService;
        this.userPrincipalService = userPrincipalService;

        progress = new Progress();
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
    public String admin(Model model) {

        model.addAttribute("submissionCategoryTypes", SubmissionCategoryType.values());
        model.addAttribute("availableUserRoles", UserRole.values());
        model.addAttribute("users", userPrincipalService.findAllUsers());
        return "admin/admin";
    }

    @RequestMapping(value = "/admin/calculatescores/", method = RequestMethod.GET)
    public String calculateScores() {
        progress.setValue(0);
        spectrumMatchCalculator.run();
        progress.setValue(0);
        return "redirect:/admin/";
    }

    @RequestMapping(value = "/admin/cluster/", method = RequestMethod.GET)
    public String cluster() {
        try {
//            spectrumClusterer.removeAll();
            for (ChromatographyType type : ChromatographyType.values())
                spectrumClusterer.cluster(type, 2, 0.25F, 0.01F);
            spectrumClusterer.removeAll();
        } catch (EmptySearchResultException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/admin/";
    }


    public static class Progress implements Serializable {

        private final static long serialVersionUID = 1L;

        private Integer value;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }
}
