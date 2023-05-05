package org.dulab.adapcompounddb.site.controllers;

import javax.validation.Valid;

import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController extends BaseController {

    final private SpectrumService spectrumService;

    @Value("${info.version}")
    private String applicationVersion;
    @Autowired
    public IndexController(final SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(final Model model) {
        model.addAttribute("countConsensusSpectra", spectrumService.countConsensusSpectra());
        model.addAttribute("countReferenceSpectra", spectrumService.countReferenceSpectra());
        model.addAttribute("appVersion", applicationVersion);
        return "index";
    }

    @RequestMapping(value = "/about/", method = RequestMethod.GET)
    public String about(Model model) {
        model.addAttribute("appVersion", applicationVersion);
        return "about";
    }
}
