package org.dulab.adapcompounddb.site.controllers.forms;

import javax.validation.constraints.NotBlank;
import org.dulab.adapcompounddb.validation.FieldMatch;
import org.dulab.adapcompounddb.validation.Password;

@FieldMatch.List({
    @FieldMatch(first = "newPass", second = "confirmedNewPass", message = "The New Password and ReEnter Password fields must match.")
})
public class ResetPasswordForm {
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
