package org.dulab.adapcompounddb.site.services;

import javax.validation.Valid;

import org.dulab.adapcompounddb.models.entities.Feedback;
import org.dulab.adapcompounddb.site.controllers.IndexController.FeedbackForm;
import org.dulab.adapcompounddb.site.repositories.FeedbackRepository;
import org.dulab.adapcompounddb.utils.ObjectMapperUtils;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackServiceImpl(final FeedbackRepository feedbackRepository) {
        super();
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public void saveFeedback(@Valid final FeedbackForm form) {

        final ObjectMapperUtils objectMapper = new ObjectMapperUtils();
        final Feedback feedback = objectMapper.map(form, Feedback.class);
        feedbackRepository.save(feedback);
    }
}
