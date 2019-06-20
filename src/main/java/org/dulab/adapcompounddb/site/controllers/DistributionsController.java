package org.dulab.adapcompounddb.site.controllers;


import org.dulab.adapcompounddb.models.entities.TagDistribution;
import org.dulab.adapcompounddb.site.services.DistributionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class DistributionsController {

    public DistributionsController(DistributionService distributionService) {
        this.distributionService = distributionService;
    }

    private final DistributionService distributionService;

    @RequestMapping(value="/study_distributions", method=RequestMethod.GET)
    public String tagDistribution(final Model model) {
        final TagDistribution tagDistribution = distributionService.getDistribution(25);
        model.addAttribute("distribution", tagDistribution);
        return "/study_distributions";
    }
}
