package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotController {

  @Autowired
  UserPrincipalService userPrincipalService;
  @GetMapping("/forgotForm")
  public String forgotPasswordForm(){
      return "forgot_password";
  }

  @PostMapping("/forgotPassword")
  public String sendEmail(@RequestParam("email") String email, Model model){
    //find user by email
    //if null return
    System.out.println("EMAIL: " +email);
    return "reset_password";
  }
  @PostMapping("/resetPassword")
  public String resetPassword(@RequestParam("password") String password, Model model){
    //find user by email
    //if null return
    System.out.println("Password: " +password);
    return null;
  }
}
