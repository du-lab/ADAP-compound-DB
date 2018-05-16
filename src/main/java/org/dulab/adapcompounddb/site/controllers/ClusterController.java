package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ClusterController {

    private final SpectrumMatchService spectrumMatchService;

    public ClusterController(SpectrumMatchService spectrumMatchService) {
        this.spectrumMatchService = spectrumMatchService;
    }

    @RequestMapping(value = "/cluster/{id:\\d+}/", method = RequestMethod.GET)
    public String cluster(@PathVariable("id") long id, Model model) {

        SpectrumCluster cluster = spectrumMatchService.getCluster(id);

        model.addAttribute("cluster", cluster);

        return "cluster/view";
    }
}
