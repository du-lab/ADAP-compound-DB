package org.dulab.adapcompounddb.site.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotController {
  @GetMapping("/forgot")
  public String forgotPasswordForm(){
      return "forgot_password";
  }

  @PostMapping("/send_otp")
  public String sendEmail(@RequestParam("email") String email){

    System.out.println("EMAIL: " +email);
    return "verify_otp";
  }

}
