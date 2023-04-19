package org.dulab.adapcompounddb.site.services;


import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.site.services.search.GroupSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


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

    public void sendEmail(String receiptant, String subject, String text)
        throws Exception {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper (message);
            InternetAddress sendTo = new InternetAddress(receiptant);
            mimeMessageHelper.setTo(sendTo);

            String email = System.getenv("ADAP_EMAIL_LOGIN");
            message.setFrom(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text);
            mailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.warn( e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.warn( e.getMessage(), e);
            throw e;
        }
    }

    public void sendOrganizationInviteEmail(final UserPrincipal user, UserPrincipal organizationUser, String url) throws MessagingException {
        String subject = "ADAP-KDP Organization Invite";
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(getInviteSubject(user, organizationUser, url), true); // set the HTML content to true
            mailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.warn( e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.warn( e.getMessage(), e);
            throw e;
        }
    }

    private String getInviteSubject(UserPrincipal user, UserPrincipal organizationUser, String url) {
        return String.format("<div style=\"width:800px; border: 1px solid #844d37;\">\n<h1 style=\"padding-left:10px;" +
                "margin-top:unset;background-color:#844d37;color: white\">ADAP-KDP</h1>\n<div style=\"padding-left:10px\">\n" +
                "<p>Hi %s,</p>\n<p>%s (%s) has invited you to join the organization. Ignore this email to decline.</p>\n" +
                "<a href=\"%s\"style=\"margin-left:30px !important;border: none;color: white;padding: 15px 32px; " +
                "text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px; " +
                "cursor: pointer;background-color:green\">Accept</a>\n<br>\n<p>Alternatively, use the link to accept " +
                "the invitation : <a href=\"%s\">HERE</a></p>\n<p>Please contact our support team at " +
                "adap.helpdesk@gmail.com if you face any issues.</p>\n</div>\n</div>",
                user.getUsername(), organizationUser.getUsername(), organizationUser.getEmail(), url, url);
    }
}
