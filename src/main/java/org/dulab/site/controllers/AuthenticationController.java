package org.dulab.site.controllers;

import org.dulab.site.services.AuthenticationService;
import org.dulab.models.entities.UserPrincipal;
import org.dulab.validation.Email;
import org.dulab.validation.FieldMatch;
import org.dulab.validation.NotBlank;
import org.dulab.validation.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@Controller
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /****************
    ***** Log In *****
     ****************/

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(Model model, HttpSession session) {
        if (UserPrincipal.from(session) != null)
            return getHomeRedirect();

        model.addAttribute("loginFailed", false);
        model.addAttribute("logInForm", new LogInForm());

        return new ModelAndView("login");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(Model model, HttpSession session, HttpServletRequest request,
                              @Valid LogInForm form, Errors errors) {

        if (UserPrincipal.from(session) != null)
            return getHomeRedirect();

        if (errors.hasErrors()) {
            form.setPassword(null);
            return new ModelAndView("login");
        }

        UserPrincipal principal;
        try {
            principal = authenticationService.authenticate(form.getUsername(), form.getPassword());
        }
        catch (ConstraintViolationException e) {
            form.setPassword(null);
            model.addAttribute("validationErrors", e.getConstraintViolations());
            return new ModelAndView("login");
        }

        if (principal == null) {
            form.setPassword(null);
            model.addAttribute("loginFailed", true);
            model.addAttribute("logInForm", form);
            return new ModelAndView("login");
        }

        UserPrincipal.assign(session, principal);
        request.changeSessionId();
        return getHomeRedirect();
    }

    /*****************
    ***** Sign Up *****
     *****************/

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signup(Model model, HttpSession session) {
        if (UserPrincipal.from(session) != null)
            return getHomeRedirect();

        model.addAttribute("signupFailed", false);
        model.addAttribute("signUpForm", new SignUpForm());

        return new ModelAndView("signup");
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView signup(Model model, HttpSession session, HttpServletRequest request,
                               @Valid SignUpForm form, Errors errors) {
        if (UserPrincipal.from(session) != null)
            return getHomeRedirect();

        if (errors.hasErrors()) {
            form.setPassword(null);
            form.setConfirmedPassword(null);
            return new ModelAndView("signup");
        }

        UserPrincipal principal = new UserPrincipal();
        principal.setUsername(form.getUsername());
        principal.setEmail(form.getEmail());
        try {
            authenticationService.saveUser(principal, form.getPassword());
        }
        catch (ConstraintViolationException e) {
            form.setPassword(null);
            form.setConfirmedPassword(null);
            model.addAttribute("validationErrors", e.getConstraintViolations());
            return new ModelAndView("signup");
        }

        UserPrincipal.assign(session, principal);
        request.changeSessionId();
        return getHomeRedirect();
    }

    /*****************
    ***** Log Out *****
     *****************/

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpSession session) {
        session.invalidate();
        return getHomeRedirect();
    }


    private ModelAndView getHomeRedirect() {
        return new ModelAndView(new RedirectView("/", true, false));
    }

    /**********************
    ***** SubmissionForm classes *****
     **********************/

    public static class LogInForm {

        @NotBlank(message = "The username is required.")
        private String username;

        @NotBlank(message = "The password is required.")
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
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

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getConfirmedEmail() {
            return confirmedEmail;
        }

        public void setConfirmedEmail(String confirmedEmail) {
            this.confirmedEmail = confirmedEmail;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmedPassword() {
            return confirmedPassword;
        }

        public void setConfirmedPassword(String confirmedPassword) {
            this.confirmedPassword = confirmedPassword;
        }
    }
}
