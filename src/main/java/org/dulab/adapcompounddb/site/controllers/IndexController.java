package org.dulab.adapcompounddb.site.controllers;

import javax.validation.Valid;

import org.dulab.adapcompounddb.models.dto.FeedbackDTO;
import org.dulab.adapcompounddb.site.services.FeedbackService;
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

    private static final String successMessage = "Thank you for your feedback. We appreciate your views and will work on it, in case any action is required based on your message.";
    final private SpectrumService spectrumService;
    final private FeedbackService feedbackService;

    @Autowired
    public IndexController(final SpectrumService spectrumService, final FeedbackService feedbackService) {
        this.spectrumService = spectrumService;
        this.feedbackService = feedbackService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(final Model model) {
        model.addAttribute("countConsensusSpectra", spectrumService.countConsensusSpectra());
        model.addAttribute("countReferenceSpectra", spectrumService.countReferenceSpectra());
        return "index";
    }

    @RequestMapping(value = "/feedback", method = RequestMethod.GET)
    public String feedback(final Model model) {
        model.addAttribute("feedbackForm", new FeedbackDTO());
        return "feedback";
    }

    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public ModelAndView feedback(final Model model, @Valid final FeedbackDTO form, final Errors errors) {
        if(errors.hasErrors()) {
            model.addAttribute("feedbackForm", form);
            model.addAttribute("errors", errors.getFieldErrors());
            return new ModelAndView("feedback");
        }
        feedbackService.saveFeedback(form);

        model.addAttribute("feedbackForm", new FeedbackDTO());
        model.addAttribute("status", successMessage);
        return new ModelAndView("feedback");
    }
}