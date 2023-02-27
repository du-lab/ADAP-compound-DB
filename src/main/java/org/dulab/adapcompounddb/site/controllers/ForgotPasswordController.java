package org.dulab.adapcompounddb.site.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
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
  public String sendEmail(@RequestParam("email") String email, HttpServletRequest request, Model model) throws Exception {
    System.out.println("EMAIL: " + email);
    //find user by email
      UserPrincipal user = userPrincipalService.findByUserEmail(email);
      if (user == null)
        throw new Exception("This email is not correct");
      //generate token
      String resetToken = UUID.randomUUID().toString();
      Date expirationDate = new Date(System.currentTimeMillis() + 86400000);//24 hour expiration time
      user.setPasswordResetToken(resetToken);
      user.setPasswordExpirationDate(expirationDate);
      userPrincipalService.saveUserPrincipal(user);
      //send email
      String domain = request.getContextPath();
      String resetUrl =  "https://localhost:8080/resetPassword?token=" +resetToken;  //use adap.cloud later
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
      ResetPassForm resetPassForm = new ResetPassForm();
      resetPassForm.setUserName(user.getUsername());
      model.addAttribute("resetPassForm", resetPassForm);
      return "reset_password";
  }
  @PostMapping("/resetPassword")
  public String changePassword(final Model model, final HttpSession session,
      @Valid final ResetPassForm form, final Errors errors) {
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
  @FieldMatch.List({
      @FieldMatch(first = "newPass", second = "confirmedNewPass", message = "The New Password and ReEnter Password fields must match.")
  })
  public static class ResetPassForm {
      private String userName;
      @NotBlank(message = "The new password is required.")
      @Password(message = "Please match your new password with the requested format.")
      private String newPass;

      @NotBlank(message = "The re-enter password is required.")
      private String confirmedNewPass;

      public String getNewPass() {
        return newPass;
      }

      public void setNewPass(String newPass) {
        this.newPass = newPass;
      }

      public String getConfirmedNewPass() {
        return confirmedNewPass;
      }

      public void setConfirmedNewPass(String confirmedNewPass) {
        this.confirmedNewPass = confirmedNewPass;
      }

      public String getUserName() {
          return userName;
      }

      public void setUserName(String userName) {
          this.userName = userName;
      }
  }
}
