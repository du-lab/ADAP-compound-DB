package org.dulab.adapcompounddb.site.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {


    public void sendEmailWithAttachment(String strBody, String subject) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("mr.toan49@gmail.com");
        mailSender.setPassword("zokjirzbhlwoliia");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("tnguy271@uncc.edu");

        msg.setSubject("Testing email");
        msg.setText("Hello World \n ");


       // MimeMessage message = mailSender.createMimeMessage();

//        try {
//
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper (message, true);
//
//            mimeMessageHelper.setTo("tnguy271@uncc.edu");
//            message.setFrom("mr.toan49@gmail.com");
//
//            mimeMessageHelper.setSubject(subject);
//
//            mimeMessageHelper.setText(strBody, true);

            //mimeMessageHelper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);


//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
        mailSender.send(msg);
        //return message;
    }
}
