package org.dulab.adapcompounddb.site.controllers;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.Statistics;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.enums.UserRole;
import org.dulab.adapcompounddb.site.services.FeedbackService;
import org.dulab.adapcompounddb.site.services.StatisticsService;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminController {

    private final StatisticsService statisticsService;
    private final UserPrincipalService userPrincipalService;
    final private FeedbackService feedbackService;

    private final Progress progress;

    @Autowired
    public AdminController(final FeedbackService feedbackService,
            final StatisticsService statisticsService,
            final UserPrincipalService userPrincipalService) {

        this.feedbackService = feedbackService;
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

    @RequestMapping(value = "/admin/feedback/{feedbackId:\\d+}/", method = RequestMethod.GET)
    public String feedback(final Model model, @PathVariable("feedbackId") final Integer feedbackId) {

        model.addAttribute("feedback", feedbackService.getFeedBackById(feedbackId));
        feedbackService.markRead(feedbackId);
        return "admin/show_feedback";
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
