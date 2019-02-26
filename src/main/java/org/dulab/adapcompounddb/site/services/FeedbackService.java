package org.dulab.adapcompounddb.site.services;

import javax.validation.Valid;

import org.dulab.adapcompounddb.site.controllers.IndexController.FeedbackForm;

public interface FeedbackService {

    void saveFeedback(@Valid FeedbackForm form);
}
