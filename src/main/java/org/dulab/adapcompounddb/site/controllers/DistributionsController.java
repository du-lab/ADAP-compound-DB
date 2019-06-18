package org.dulab.adapcompounddb.site.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DistributionsController {

    @RequestMapping(value = "/study_distributions", method = RequestMethod.GET)
    public String view(final Model model) {

    return "/study_distributions";
    }
}
