package org.dulab.adapcompounddb.site.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;


@Component
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupSearchService.class);
    @Autowired
    JavaMailSender mailSender;

    public void sendEmailWithAttachment(String filepath, String receiptant) {


        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper (message, true);
            InternetAddress sendTo = new InternetAddress(receiptant);
            mimeMessageHelper.setTo(sendTo);

            String email = System.getenv("ADAP_EMAIL_LOGIN");
            message.setFrom(email);
            mimeMessageHelper.setSubject("ADAP-KDB Matching Results");
            mimeMessageHelper.setText("<p>Dear ADAP-KDB User,</p>\n" +
                    "\n" +
                    "<p>See the attached output of the ADAP-KDB spectral search, automatically generated on " + new java.util.Date()  +".</p>\n" +
                    "\n" +
                    "<p>You received this email because you selected &quot;Send matching results to Email&quot; option when performing the spectral search in ADAP-KDB. Please, unselect that option if you do not wish to receive such emails.</p>\n" +
                    "\n" +
                    "<p>With regards,</p>\n" +
                    "\n" +
                    "<p>ADAP-KDB Team<br>\n" +
                    "</p>",true);

            FileSystemResource  fileSystemResource  = new FileSystemResource(filepath);
            mimeMessageHelper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);

            mailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.warn( e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.warn( e.getMessage(), e);
        }


    }

    public void sendEmail(String receiptant, String subject, String text){
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper (message);
            InternetAddress sendTo = new InternetAddress(receiptant);
            mimeMessageHelper.setTo(sendTo);

            String email = System.getenv("ADAP_EMAIL_LOGIN");
            message.setFrom("mr.toan49@gmail.com");
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text);
            mailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.warn( e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.warn( e.getMessage(), e);
        }
    }
}
