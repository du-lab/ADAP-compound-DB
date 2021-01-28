package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.dulab.adapcompounddb.site.controllers.forms.FilterOptions;
import org.dulab.adapcompounddb.site.services.*;
import org.dulab.adapcompounddb.site.services.search.SpectrumMatchService;
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
    private final SubmissionTagService submissionTagService;
    private final StatisticsService statisticsService;
    private final DistributionService distributionService;

    public ClusterController(final SpectrumMatchService spectrumMatchService,
                             final SubmissionService submissionService, final StatisticsService statisticsService,
                             final DistributionService distributionService,
                             final SubmissionTagService submissionTagService) {

        this.spectrumMatchService = spectrumMatchService;
        this.statisticsService = statisticsService;
        this.submissionService = submissionService;
        this.distributionService = distributionService;
        this.submissionTagService = submissionTagService;
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

        List<String> speciesList = submissionTagService.findDistinctTagValuesByTagKey("species (common)");
        List<String> sourceList = submissionTagService.findDistinctTagValuesByTagKey("sample source");
        List<String> diseaseList = submissionTagService.findDistinctTagValuesByTagKey("disease");

        model.addAttribute("filterOptions", new FilterOptions(speciesList, sourceList, diseaseList));
        return "all_clusters";
    }

    @RequestMapping(value = "/cluster/{id:\\d+}/", method = RequestMethod.GET)
    public String cluster(@PathVariable("id") final long id, final Model model) {
        final SpectrumCluster cluster = spectrumMatchService.getCluster(id);
        spectrumMatchService.loadTagsofCluster(cluster);

        // sort Tag distributions by its Pvalue
        cluster.getTagDistributions()
                .sort(Comparator.comparingDouble(t -> t != null ? t.getPValue() : 1.0));

        model.addAttribute("cluster", cluster);
        return "cluster/cluster";
    }
}
