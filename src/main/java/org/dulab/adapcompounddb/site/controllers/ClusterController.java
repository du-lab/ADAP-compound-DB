package org.dulab.adapcompounddb.site.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.StatisticsService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ClusterController {

    private final SpectrumMatchService spectrumMatchService;
    private final SubmissionService submissionService;
    private final StatisticsService statisticsService;

    public ClusterController(final SpectrumMatchService spectrumMatchService,
            final SubmissionService submissionService, final StatisticsService statisticsService) {

        this.spectrumMatchService = spectrumMatchService;
        this.statisticsService = statisticsService;
        this.submissionService = submissionService;
    }

    @ModelAttribute
    public void addAttributes(final Model model) {
        model.addAttribute("submissionCategoryTypes", SubmissionCategoryType.values());

        final Map<SubmissionCategoryType, List<SubmissionCategory>> submissionCategoryMap = new HashMap<>();
        for (final SubmissionCategory category : submissionService.findAllCategories()) {
            submissionCategoryMap
            .computeIfAbsent(category.getCategoryType(), c -> new ArrayList<>())
            .add(category);
        }

        model.addAttribute("submissionCategoryMap", submissionCategoryMap);
    }

    @RequestMapping(value="/allClusters/", method=RequestMethod.GET)
    public String clusters(final Model model) {
        final List<SpectrumCluster> allClusters = spectrumMatchService.getAllClusters();
        model.addAttribute("clusters", allClusters);
        return "cluster/all_clusters";
    }

    @RequestMapping(value = "/cluster/{id:\\d+}/", method = RequestMethod.GET)
    public String cluster(@PathVariable("id") final long id, final Model model) {

        final SpectrumCluster cluster = spectrumMatchService.getCluster(id);
        final List<String> tags = submissionService.findTagsFromACluster(id);
        model.addAttribute("cluster", cluster);
        //        model.addAttribute("tags", tags);

        return "cluster/cluster";
    }
}
