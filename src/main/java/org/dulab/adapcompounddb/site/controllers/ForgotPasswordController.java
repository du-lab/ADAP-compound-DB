package org.dulab.adapcompounddb.site.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.controllers.forms.ResetPasswordForm;
import org.dulab.adapcompounddb.site.services.AuthenticationService;
import org.dulab.adapcompounddb.site.services.EmailService;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.dulab.adapcompounddb.validation.FieldMatch;
import org.dulab.adapcompounddb.validation.Password;
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
public class ForgotPasswordController {
  @Autowired
  UserPrincipalService userPrincipalService;
  @Autowired
  EmailService emailService;

  @Autowired
  AuthenticationService authenticationService;
  @GetMapping("/forgotForm")
  public String forgotPasswordForm() {
    return "forgot_password";
  }

  @PostMapping("/forgotPassword")
  public String sendEmail(@RequestParam("username") String userName, HttpServletRequest request, Model model) throws Exception {
    System.out.println("USERNAME: " + userName);
    //find user by userName
      UserPrincipal user = userPrincipalService.findUserByUsername(userName);
      if (user == null)
        throw new Exception("The user name doesn't exist");
      //generate token
      String resetToken = UUID.randomUUID().toString();
      Date expirationDate = new Date(System.currentTimeMillis() + 3600000);//1 hour expiration time
      user.setPasswordResetToken(resetToken);
      user.setPasswordExpirationDate(expirationDate);
      userPrincipalService.saveUserPrincipal(user);
      //send email
      String domain = request.getContextPath();
      String resetUrl =  "http://localhost:8080/resetPassword?token=" +resetToken;  //TODO: change to https://adap.cloud
      String subject = "Reset Password";
      String text = "Please use this link to reset your password: " + resetUrl;
      emailService.sendEmail(user.getEmail(), subject, text);

      return "reset_password_link_sent";
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
