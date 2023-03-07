package org.dulab.adapcompounddb.site.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.controllers.forms.ResetPasswordForm;
import org.dulab.adapcompounddb.site.services.AuthenticationService;
import org.dulab.adapcompounddb.site.services.EmailService;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.UUID;

@Controller
public class ForgotController {
  @Autowired
  UserPrincipalService userPrincipalService;
  @Autowired
  EmailService emailService;

  @Autowired
  AuthenticationService authenticationService;
  @GetMapping("/forgotPassForm")
  public String forgotPasswordForm() {
    return "forgot_password";
  }

  @GetMapping("/forgotUsernameForm")
  public String forgotUsernameForm() {
    return "forgot_username";
  }

  @PostMapping("/forgotPassword")
  public String sendEmail(@RequestParam("email_username_input") String input, HttpServletRequest request, Model model) throws Exception {

    //find user by email or username
      UserPrincipal user = userPrincipalService.findByUserEmailOrUsername(input);
      if (user == null)
        throw new Exception("This email or username does not exist");
      //generate token
      String resetToken = UUID.randomUUID().toString();
      Date expirationDate = new Date(System.currentTimeMillis() + 3600000);//1 hour expiration time
      user.setPasswordResetToken(resetToken);
      user.setPasswordExpirationDate(expirationDate);
      userPrincipalService.saveUserPrincipal(user);
      //send email
      String domain = request.getContextPath();
      String resetUrl =  "http://localhost:8080/resetPassword?token=" +resetToken;  //TODO: change to https://adap.cloud
      String subject = "ADAP-KDB password reset";
      String text = "Please use this link to reset your password: " + resetUrl +
          "\nIf you didn't make this request, please contact our support team at "
          + "adap.helpdesk@gmail.com";
      emailService.sendEmail(user.getEmail(), subject, text);

      return "reset_password_link_sent";
  }

  @PostMapping("/forgotUsername")
  public String forgotUsername(@RequestParam("email") String email, HttpServletRequest request, Model model) throws Exception{
    UserPrincipal user = userPrincipalService.findByUserEmail(email);
    if (user == null)
      throw new Exception("This email is not correct");
    String subject = "ADAP-KDB username request";
    String text = "This email address is associated with the following username: " + user.getUsername() +
        "\nIf you didn't make this request, please contact our support team at "
        + "adap.helpdesk@gmail.com";
    emailService.sendEmail(user.getEmail(), subject, text);

    return "retrieve_username_link_sent";
  }
  //after using click on reset link in their email
  @GetMapping("/resetPassword")
  public String resetPassword(Model model, @RequestParam("token") String token) throws Exception {
      //validate token
      UserPrincipal user = userPrincipalService.findByToken(token);
      if(user == null)
          throw new Exception("Invalid token");
      if(user.getPasswordExpirationDate().before(new Date()))
          throw new Exception("Token has expired");

      //if validate success return reset password page
      ResetPasswordForm resetPassForm = new ResetPasswordForm();
      resetPassForm.setUserName(user.getUsername());
      model.addAttribute("resetPasswordForm", resetPassForm);
      return "reset_password";
  }
  @PostMapping("/resetPassword")
  public String changePassword(final Model model, final HttpSession session,
      @Valid final ResetPasswordForm form, final Errors errors) {
      if (errors.hasErrors()) {
          form.setNewPass(null);
          form.setConfirmedNewPass(null);
          return "reset_password";
      }

      try {
          authenticationService.resetPassword(form.getUserName(), form.getConfirmedNewPass());
      } catch (Exception e) {
          form.setNewPass(null);
          form.setConfirmedNewPass(null);
          model.addAttribute("errorMsg", e.getMessage());
          model.addAttribute("resetPassForm", form);
          return "reset_password";

      }

      return "password_reset_sucess";
  }

}
