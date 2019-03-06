package org.dulab.adapcompounddb.site.controllers;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.dulab.adapcompounddb.site.services.FeedbackService;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
        model.addAttribute("feedbackForm", new FeedbackForm());
        return "feedback";
    }

    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public String feedback(final Model model, @Valid final FeedbackForm form, final Errors errors) {
        if(errors.hasErrors()) {
            return "feedback";
        }
        feedbackService.saveFeedback(form);

        model.addAttribute("feedbackForm", new FeedbackForm());
        model.addAttribute("status", successMessage);
        return "feedback";
    }

    @Getter(value=AccessLevel.PUBLIC)
    @Setter(value=AccessLevel.PUBLIC)
    @RequiredArgsConstructor
    public static class FeedbackForm {

        @NotBlank(message = "Please provide your Name.")
        private String name;

        private String affiliation;

        @NotBlank(message = "Please provide your Email for contact.")
        private String email;

        @NotBlank(message = "Your Feedback Message is required.")
        private String message;
    }
}