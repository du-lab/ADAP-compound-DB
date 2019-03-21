package org.dulab.adapcompounddb.site.services;

import java.util.Date;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private static final String DESC = "DESC";
    private final FeedbackRepository feedbackRepository;

    private final Properties properties;

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

    public FeedbackServiceImpl(final FeedbackRepository feedbackRepository, @Qualifier("email_properties") final Properties properties) {
        super();
        this.feedbackRepository = feedbackRepository;
        this.properties = properties;
    }

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
        feedback.setReadFlag(false);
        final Date today = new Date();
        feedback.setSubmitDate(today);
        feedbackRepository.save(feedback);
        final Integer count = feedbackRepository.getFeedbackCountOfDay(today);
        if(count < EMAIL_MAX_COUNT) {
            sendFeedbackEmail(feedback);
        }
    }

    @Override
    public void sendFeedbackEmail(final Feedback feedback) {
        final Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("username"), properties.getProperty("password"));
            }
        });

        try {
            final MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(properties.getProperty("email_from")));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(properties.getProperty("email_to")));
            message.setSubject(FeedbackService.SUBJECT);
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
