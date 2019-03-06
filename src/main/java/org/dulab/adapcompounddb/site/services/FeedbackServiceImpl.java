package org.dulab.adapcompounddb.site.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import org.dulab.adapcompounddb.models.entities.Feedback;
import org.dulab.adapcompounddb.site.controllers.IndexController.FeedbackForm;
import org.dulab.adapcompounddb.site.repositories.FeedbackRepository;
import org.dulab.adapcompounddb.utils.ObjectMapperUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private static final String TO_EMAIL = "nilanjan.mhatre@gmail.com";
    private static final String FROM = "nmhatre@uncc.edu";
    private static final String USERNAME = "AKIAI43QCQJF5D6YM7GA";
    private static final String PASSWORD = "BCDf5VdwYyzc+iJNekSB6Ah4HfExjd7DGe3Y+3EPSCok";
    private static final String SUBJECT = "ADAP ccompound Spectral library - You have received a new message";

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
        sendFeedbackEmail(feedback);
    }

    @Override
    public void sendFeedbackEmail(final Feedback feedback) {
        final Properties prop = getEmailProperties();
        final Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            final MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(FROM));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(TO_EMAIL));
            message.setSubject(SUBJECT);
            message.setContent(getHTMLMessage(feedback), "text/html");

            Transport.send(message);
        } catch (final MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public String getHTMLMessage(final Feedback feedback) {
        return htmlTemplate.replace("{name}", feedback.getName())
                .replace("{email}", feedback.getEmail())
                .replace("{affiliation}", feedback.getAffiliation())
                .replace("{message}", feedback.getMessage());
    }

    @Bean
    public Properties getEmailProperties() {
        final Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "email-smtp.us-east-1.amazonaws.com");
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.ssl.trust", "email-smtp.us-east-1.amazonaws.com");

        return prop;
    }
}
