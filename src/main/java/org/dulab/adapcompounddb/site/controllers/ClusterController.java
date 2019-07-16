package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.dulab.adapcompounddb.models.entities.TagDistribution;
import org.dulab.adapcompounddb.site.services.DistributionService;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.StatisticsService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@Controller
public class ClusterController {

    private final SpectrumMatchService spectrumMatchService;
    private final SubmissionService submissionService;
    private final StatisticsService statisticsService;
    private final DistributionService distributionService;

    public ClusterController(final SpectrumMatchService spectrumMatchService,
                             final SubmissionService submissionService, final StatisticsService statisticsService,
                             final DistributionService distributionService) {

        this.spectrumMatchService = spectrumMatchService;
        this.statisticsService = statisticsService;
        this.submissionService = submissionService;
        this.distributionService = distributionService;
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

    @RequestMapping(value = "/allClusters/", method = RequestMethod.GET)
    public String clusters(final Model model) {
        final List<SpectrumCluster> allClusters = spectrumMatchService.getAllClusters();
        model.addAttribute("clusters", allClusters);
        return "cluster/all_clusters";
    }

    @RequestMapping(value = "/cluster/{id:\\d+}/", method = RequestMethod.GET)
    public String cluster(@PathVariable("id") final long id, final Model model) {
        final SpectrumCluster cluster = spectrumMatchService.getCluster(id);
        spectrumMatchService.loadTagsofCluster(cluster);

        // sort Tag distributions by its Pvalue
        cluster.getTagDistributions()
                .sort(Comparator.comparingDouble(TagDistribution::getPValue));

        model.addAttribute("cluster", cluster);
        return "cluster/cluster";
    }
}
