package org.dulab.adapcompounddb.site.services;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailTest {


    public void sendEmail(String toEmail, String subject, String message){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("mr.toan49@gmail.com");
        mailSender.setPassword("rpiuodtwhtdlpgdc");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("tnguy271@uncc.edu");

        msg.setSubject("Testing email");
        msg.setText("Hello World \n ");

        mailSender.send(msg);
    }

    public static void main(String [] args){
        EmailTest e = new EmailTest();
        e.sendEmail(null,null,null);
    }
}
