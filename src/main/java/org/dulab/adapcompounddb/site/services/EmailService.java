package org.dulab.adapcompounddb.site.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


@Component
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    public void sendEmailWithAttachment(String filepath, String receiptant) {


        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper (message, true);
            InternetAddress sendTo = new InternetAddress(receiptant);
            mimeMessageHelper.setTo(sendTo);

            //change email here
            message.setFrom("adap.helpdesk@gmail.com");
            mimeMessageHelper.setSubject("Generated output");
            mimeMessageHelper.setText("Here is the generated output for the group search",true);

            FileSystemResource  fileSystemResource  = new FileSystemResource(filepath);
            mimeMessageHelper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
