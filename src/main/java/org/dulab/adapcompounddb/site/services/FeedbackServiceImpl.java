package org.dulab.adapcompounddb.site.services;

import java.util.List;
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

import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.FeedbackDTO;
import org.dulab.adapcompounddb.models.entities.Feedback;
import org.dulab.adapcompounddb.site.repositories.FeedbackRepository;
import org.dulab.adapcompounddb.utils.ObjectMapperUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private static final String DESC = "DESC";
    private static final String TO_EMAIL = "nilanjan.mhatre@gmail.com";
    private static final String FROM = "nmhatre@uncc.edu";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";
    private static final String SUBJECT = "ADAP ccompound Spectral library - You have received a new message";

    private final FeedbackRepository feedbackRepository;

    private static enum ColumnInformation {
        ID(0, "id"), MESSAGE(1, "message"), NAME(2, "name"), DATE(3, "submitDate");

        private int position;
        private String sortColumnName;

        private ColumnInformation(final int position, final String sortColumnName) {
            this.position = position;
            this.sortColumnName = sortColumnName;
        }

        public int getPosition() {
            return position;
        }

        public String getSortColumnName() {
            return sortColumnName;
        }

        public static String getColumnNameFromPosition(final int position) {
            String columnName = null;
            for (final ColumnInformation columnInformation : ColumnInformation.values()) {
                if (position == columnInformation.getPosition()) {
                    columnName = columnInformation.getSortColumnName();
                }
            }
            return columnName;
        }

        public static String getDefaultSortColumn() {
            return ColumnInformation.DATE.getSortColumnName();
        }
    }

    public FeedbackServiceImpl(final FeedbackRepository feedbackRepository) {
        super();
        this.feedbackRepository = feedbackRepository;
    @Override
    public FeedbackDTO getFeedBackById(final Integer id) {
        final ObjectMapperUtils objectMapper = new ObjectMapperUtils();
        return objectMapper.map(feedbackRepository.findById(id).get(), FeedbackDTO.class);
    }

    @Override
    public void markRead(final Integer id) {
        feedbackRepository.markRead(id);
    }

    @Override
    public void saveFeedback(@Valid final FeedbackDTO form) {
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

    @Override
    public DataTableResponse findAllFeedbackForResponse(final String searchStr, final Integer start,
            final Integer length, final Integer column, String sortDirection) {
        final ObjectMapperUtils objectMapper = new ObjectMapperUtils();
        Pageable pageable = null;

        String sortColumn = ColumnInformation.getColumnNameFromPosition(column);
        if (sortColumn == null) {
            sortColumn = ColumnInformation.getDefaultSortColumn();
            sortDirection = DESC;
        }
        if (sortColumn != null) {
            final Sort sort = new Sort(Sort.Direction.fromString(sortDirection), sortColumn);
            pageable = PageRequest.of(start / length, length, sort);
        } else {
            pageable = PageRequest.of(start / length, length);
        }

        final Page<Feedback> feedbackPage = feedbackRepository.findAllFeedback(searchStr, pageable);
        final List<FeedbackDTO> feedbackList = objectMapper.map(feedbackPage.getContent(), FeedbackDTO.class);

        final DataTableResponse response = new DataTableResponse(feedbackList);
        response.setRecordsTotal(feedbackPage.getTotalElements());
        response.setRecordsFiltered(feedbackPage.getTotalElements());

        return response;
    }
}
