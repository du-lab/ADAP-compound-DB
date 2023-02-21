package org.dulab.adapcompounddb.site.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.controllers.AuthenticationController.ChangePassForm;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.dulab.adapcompounddb.validation.FieldMatch;
import org.dulab.adapcompounddb.validation.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotController {

  @Autowired
  UserPrincipalService userPrincipalService;

  @GetMapping("/forgotForm")
  public String forgotPasswordForm() {
    return "forgot_password";
  }

  @PostMapping("/forgotPassword")
  public String sendEmail(@RequestParam("email") String email, Model model) throws Exception {
    //find user by email
    UserPrincipal user = userPrincipalService.findByUserEmail(email);
    if (user == null)
      throw new Exception("This email is not correct");
    //if null return
    model.addAttribute("resetPassForm", new ResetPassForm());
    System.out.println("EMAIL: " + email);
    return "reset_password";
  }

  @PostMapping("/resetPassword")
  public String resetPassword(final Model model, final HttpSession session,
      @Valid final ResetPassForm form, final Errors errors) {
    if (errors.hasErrors()) {
      return "reset_password";
    }

    return "password_reset_sucess";
  }
  @FieldMatch.List({
      @FieldMatch(first = "newPass", second = "confirmedNewPass", message = "The New Password and ReEnter Password fields must match.")
  })
  public static class ResetPassForm {

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
  }
}
