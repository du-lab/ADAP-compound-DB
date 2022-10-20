package org.dulab.adapcompounddb.site.controllers;

import javax.validation.Valid;

import org.dulab.adapcompounddb.models.dto.FeedbackDTO;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController extends BaseController {

    final private SpectrumService spectrumService;


    @Autowired
    public IndexController(final SpectrumService spectrumService) {
        this.spectrumService = spectrumService;

    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(final Model model) {
        model.addAttribute("countConsensusSpectra", spectrumService.countConsensusSpectra());
        model.addAttribute("countReferenceSpectra", spectrumService.countReferenceSpectra());
        return "index";
    }

    @RequestMapping(value = "/about/", method = RequestMethod.GET)
    public String about() {
        return "about";
    }


}