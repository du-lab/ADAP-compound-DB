package org.dulab.adapcompounddb.site.controllers;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.Statistics;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.UserRole;
import org.dulab.adapcompounddb.site.services.SpectrumMatchCalculator;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.StatisticsService;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminController {

    private final SpectrumMatchCalculator spectrumMatchCalculator;
    private final SpectrumMatchService spectrumMatchService;
    private final StatisticsService statisticsService;
    private final UserPrincipalService userPrincipalService;

    private final Progress progress;

    @Autowired
    public AdminController(final SpectrumMatchCalculator spectrumMatchCalculator,
            final SpectrumMatchService spectrumMatchService,
            final StatisticsService statisticsService,
            final UserPrincipalService userPrincipalService) {

        this.spectrumMatchCalculator = spectrumMatchCalculator;
        this.spectrumMatchService = spectrumMatchService;
        this.statisticsService = statisticsService;
        this.userPrincipalService = userPrincipalService;

        progress = new Progress();
    }

    @ModelAttribute
    public void addAttributes(final Model model) {

        final Map<ChromatographyType, Statistics> statisticsMap = new TreeMap<>();
        for (final ChromatographyType type : ChromatographyType.values()) {
            statisticsMap.put(type, statisticsService.getStatistics(type));
        }

        model.addAttribute("statistics", statisticsMap);
        //        model.addAttribute("clusters", spectrumMatchService.getAllClusters());
    }

    @RequestMapping(value = "/admin/", method = RequestMethod.GET)
    public String admin(final Model model) {

        model.addAttribute("submissionCategoryTypes", SubmissionCategoryType.values());
        model.addAttribute("availableUserRoles", UserRole.values());
        model.addAttribute("users", userPrincipalService.findAllUsers());
        return "admin/admin";
    }

    @RequestMapping(value = "/admin/calculatescores/", method = RequestMethod.GET)
    public String calculateScores() {
        //        spectrumMatchService.fillSpectrumMatchTable(0.01F, 0.75F);
        progress.setValue(0);
        spectrumMatchCalculator.run();
        progress.setValue(0);
        return "redirect:/admin/";
    }

    @RequestMapping(value = "/admin/cluster/", method = RequestMethod.GET)
    public String cluster() {
        try {
            spectrumMatchService.cluster(0.1F, 2, 0.75F);
        } catch (final EmptySearchResultException e) {
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

        public void setValue(final Integer value) {
            this.value = value;
        }
    }
}
