package org.dulab.adapcompounddb.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@RestController
public class EmailController {


    private final JavaMailSender javaMailSender;


    public EmailController(final JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    @PostMapping(value = "/sendEmail")
    //public ResponseEntity<String> sendEmail(final @RequestBody CommonsMultipartFile file) {
    public ResponseEntity<String> sendEmail() {
        //JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setTo("tnguy271@uncc.edu");
//
//        msg.setSubject("Testing email");
//        msg.setText("Hello World \n ");
//
//        javaMailSender.send(msg);
        return new ResponseEntity<String>("DONE",HttpStatus.ACCEPTED);
    }
}
