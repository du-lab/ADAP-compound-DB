package org.dulab.site.authentication;

import org.dulab.site.models.UserPrincipal;
import org.dulab.site.validation.Email;
import org.dulab.site.validation.FieldMatch;
import org.dulab.site.validation.NotBlank;
import org.dulab.site.validation.Password;
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
import java.security.Principal;

@Controller
public class AuthenticationController {

    private AuthenticationService authenticationService;

    public AuthenticationController() {
        authenticationService = new DefaultAuthenticationService();
    }

    /****************
    ***** Log In *****
     ****************/

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(Model model, HttpSession session) {
        if (UserPrincipal.getPrincipal(session) != null)
            return getHomeRedirect();

        model.addAttribute("loginFailed", false);
        model.addAttribute("logInForm", new LogInForm());

        return new ModelAndView("login");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(Model model, HttpSession session, HttpServletRequest request,
                              @Valid LogInForm form, Errors errors) {

        if (UserPrincipal.getPrincipal(session) != null)
            return getHomeRedirect();

        if (errors.hasErrors()) {
            form.setPassword(null);
            return new ModelAndView("login");
        }

        Principal principal;
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

        UserPrincipal.setPrincipal(session, principal);
        request.changeSessionId();
        return getHomeRedirect();
    }

    /*****************
    ***** Sign Up *****
     *****************/

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signup(Model model, HttpSession session) {
        if (UserPrincipal.getPrincipal(session) != null)
            return getHomeRedirect();

        model.addAttribute("signupFailed", false);
        model.addAttribute("signUpForm", new SignUpForm());

        return new ModelAndView("signup");
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView signup(Model model, HttpSession session, HttpServletRequest request,
                               @Valid SignUpForm form, Errors errors) {
        if (UserPrincipal.getPrincipal(session) != null)
            return getHomeRedirect();

        if (errors.hasErrors()) {
            form.setPassword(null);
            form.setConfirmedPassword(null);
            return new ModelAndView("signup");
        }

        UserPrincipal principal = new UserPrincipal();
        principal.setUsername(form.getUsername());
        try {
            authenticationService.saveUser(principal, form.getPassword());
        }
        catch (ConstraintViolationException e) {
            form.setPassword(null);
            form.setConfirmedPassword(null);
            model.addAttribute("validationErrors", e.getConstraintViolations());
            return new ModelAndView("signup");
        }

        UserPrincipal.setPrincipal(session, principal);
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
    ***** Form classes *****
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
