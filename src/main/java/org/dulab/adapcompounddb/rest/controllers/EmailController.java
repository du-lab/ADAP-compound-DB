package org.dulab.adapcompounddb.rest.controllers;

import org.dulab.adapcompounddb.site.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.mail.internet.MimeMessage;

@RestController
public class EmailController {

    private final EmailService emailService;


    @Autowired
    public EmailController(final EmailService emailService){
        this.emailService = emailService;
    }

    @RequestMapping(value = "/sendEmail" ,method = RequestMethod.GET)
    //public ResponseEntity<String> sendEmail(final @RequestBody CommonsMultipartFile file) {
    public ResponseEntity<?> sendEmail(Model model) {
        String body = "Hi, " + "\nThis is a test email." + "\nThanks & Regards, " + "\nToan";

           emailService.sendEmailWithAttachment(body, "Test email");
        System.out.println("***IN CONTROLLER");
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
