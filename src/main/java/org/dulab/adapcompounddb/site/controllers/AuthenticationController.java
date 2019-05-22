package org.dulab.adapcompounddb.site.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.AuthenticationService;
import org.dulab.adapcompounddb.validation.Email;
import org.dulab.adapcompounddb.validation.FieldMatch;
import org.dulab.adapcompounddb.validation.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Controller
public class AuthenticationController {

    private static final Logger LOG = LogManager.getLogger();

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /****************
     ***** Log In *****
     ****************/

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(final Model model, final HttpSession session, @RequestParam(name = "loginFailed", required = false, defaultValue = "false") final Boolean loginFailed) {
        if (UserPrincipal.from(session) != null) {
            return getHomeRedirect();
        }

        model.addAttribute("loginFailed", loginFailed);
        model.addAttribute("logInForm", new LogInForm());

        return new ModelAndView("login");
    }

    //    @RequestMapping(value = "/login", method = RequestMethod.POST)
    //    public ModelAndView login(Model model, HttpSession session, HttpServletRequest request,
    //                              @Valid LogInForm form, Errors errors, @RequestParam(name = "loginFailed", defaultValue = "false") Boolean loginFailed) {
    //
    //        if (UserPrincipal.from(session) != null)
    //            return getHomeRedirect();
    //
    //        if (errors.hasErrors()) {
    //            form.setPassword(null);
    //            return new ModelAndView("login");
    //        }
    //
    //        UserPrincipal principal;
    //        try {
    //            principal = authenticationService.authenticate(form.getUsername(), form.getPassword());
    //        } catch (ConstraintViolationException e) {
    //            form.setPassword(null);
    //            model.addAttribute("validationErrors", e.getConstraintViolations());
    //            return new ModelAndView("login");
    //        }
    //
    //        model.addAttribute("loginFailed", loginFailed);
    //        if (principal == null) {
    //            form.setPassword(null);
    //            model.addAttribute("loginFailed", true);
    //            model.addAttribute("logInForm", form);
    //            return new ModelAndView("login");
    //        }
    //
    //        UserPrincipal.assign(session, principal);
    //        request.changeSessionId();
    //        return getHomeRedirect();
    //    }

    /*****************
     ***** Sign Up *****
     *****************/

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signup(final Model model, final HttpSession session) {
        if (UserPrincipal.from(session) != null) {
            return getHomeRedirect();
        }

        model.addAttribute("signupFailed", false);
        model.addAttribute("signUpForm", new SignUpForm());

        return new ModelAndView("signup");
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView signup(final Model model, final HttpSession session, final HttpServletRequest request,
                               @Valid final SignUpForm form, final Errors errors) {
        if (UserPrincipal.from(session) != null) {
            return getHomeRedirect();
        }

        if (errors.hasErrors()) {
            form.setPassword(null);
            form.setConfirmedPassword(null);
            return new ModelAndView("signup");
        }

        final UserPrincipal principal = new UserPrincipal();
        principal.setUsername(form.getUsername());
        principal.setEmail(form.getEmail());
        try {
            authenticationService.saveUser(principal, form.getPassword());
        } catch (Throwable t) {
            LOG.warn("Error during authentication", t);

            if (t instanceof ConstraintViolationException) {
                model.addAttribute("validationErrors",
                        ((ConstraintViolationException) t).getConstraintViolations());
            } else if (t instanceof DataIntegrityViolationException) {
                while (t.getCause() != null) {
                    t = t.getCause();
                }
                model.addAttribute("errorMsg",
                        t.getMessage().contains("Duplicate")
                                ? "Username is already used."
                                : t.getMessage());
            }

            form.setPassword(null);
            form.setConfirmedPassword(null);
            return new ModelAndView("signup");
        }

        /*UserPrincipal.assign(session, principal);
        request.changeSessionId();*/
        return new ModelAndView("redirect:/login/");
    }

    /*****************************
     ***** Changing Password *****
     ****************************/

    @RequestMapping(value = "/account/changePassword", method = RequestMethod.GET)
    public ModelAndView changePassword(final Model model, final HttpSession session) {
        if (UserPrincipal.from(session) != null) {
            return getHomeRedirect();
        }
        model.addAttribute("changePassFail", false);
        model.addAttribute("changePassForm", new ChangePassForm());

        return new ModelAndView("account/changePassword");
    }

    @RequestMapping(value = "/account/changePassword", method = RequestMethod.POST)
    public ModelAndView changePassword(final Model model, final HttpSession session,
                                       @Valid final ChangePassForm form, final Errors errors) {
        if (UserPrincipal.from(session) != null) {
            return getHomeRedirect();
        }

        if (errors.hasErrors()) {
            return new ModelAndView("account/changePassword");
        }
        User user = (User) session.getAttribute("currentUser");
        try {
            authenticationService.changePassword(user.getUsername(),form.getOldpass(),form.getNewpass());
        } catch (Throwable t) {
            if (t instanceof ConstraintViolationException) {
                model.addAttribute("validationErrors",
                        ((ConstraintViolationException) t).getConstraintViolations());
            } else if (t instanceof DataIntegrityViolationException) {
                while (t.getCause() != null) {
                    t = t.getCause();
               }
            }
            form.setRenewpass(null);
            return new ModelAndView("account/changePassword");
       }
        return new ModelAndView("account/changePassword");
    }


    /*****************
     ***** Log Out *****
     *****************/

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public ModelAndView logout(final HttpSession session) {
        session.invalidate();
        return getHomeRedirect();
    }

    private ModelAndView getHomeRedirect() {
        return new ModelAndView(new RedirectView("/", true, false));
    }

    /**********************
     ***** SubmissionForm classes *****
     **********************/

    @FieldMatch.List({
            @FieldMatch(first = "newpass", second = "renewpass", message = "The New Password and ReEnter Password fields must match.")
    })

    public static class ChangePassForm {
        private String username;
        private String email;

        @NotBlank(message = "The old password is required.")
        private String oldpass;

        @NotBlank(message = "The new password is required.")
        @Password(message = "Please match your new password with the requested format.")
        private String newpass;

        @NotBlank(message = "The re-enter password is required.")
        private String renewpass;

        public String getUsername() {
            return username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(final String email) {
            this.email = email;
        }

        public String getOldpass() {
            return oldpass;
        }

        public void setOldpass(final String oldpass) {
            this.oldpass = oldpass;
        }

        public String getNewpass() {
            return newpass;
        }

        public void setNewpass(final String newpass) {
            this.newpass = newpass;
        }

        public String getRenewpass() {
            return renewpass;
        }

        public void setRenewpass(final String renewpass) {
            this.renewpass = renewpass;
        }
    }

    public static class LogInForm {

        @NotBlank(message = "The username is required.")
        private String username;

        @NotBlank(message = "The password is required.")
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(final String password) {
            this.password = password;
        }
    }

    @FieldMatch.List({
            @FieldMatch(first = "email", second = "confirmedEmail", message = "The E-mail fields must match."),
            @FieldMatch(first = "password", second = "confirmedPassword",
                    message = "The Password fields must match.")
    })

    public static class SignUpForm {

        @NotBlank(message = "The username is required.")
        private String username;

        @NotBlank(message = "E-mail address is required.")
        @Email
        private String email;

        private String confirmedEmail;

        @NotBlank(message = "The password is required.")
        @Password(message = "Please match the requested format.")
        private String password;

        private String confirmedPassword;

        public String getUsername() {
            return username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(final String email) {
            this.email = email;
        }

        public String getConfirmedEmail() {
            return confirmedEmail;
        }

        public void setConfirmedEmail(final String confirmedEmail) {
            this.confirmedEmail = confirmedEmail;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(final String password) {
            this.password = password;
        }

        public String getConfirmedPassword() {
            return confirmedPassword;
        }

        public void setConfirmedPassword(final String confirmedPassword) {
            this.confirmedPassword = confirmedPassword;
        }
    }

    /**********************
     ***** Functions *****
     **********************/

}
