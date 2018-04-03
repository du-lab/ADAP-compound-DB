package org.dulab.site.authentication;

import org.dulab.site.models.UserPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
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

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ModelAndView login(Model model, HttpSession session) {
        if (UserPrincipal.getPrincipal(session) != null)
            return getHomeRedirect();

        model.addAttribute("loginFailed", false);
        model.addAttribute("logInForm", new LogInForm());
        model.addAttribute("signUpForm", new SignUpForm());

        return new ModelAndView("login");
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
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
            model.addAttribute("signUpForm", new SignUpForm());
            return new ModelAndView("login");
        }

        UserPrincipal.setPrincipal(session, principal);
        request.changeSessionId();
        return getHomeRedirect();
    }

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public ModelAndView signup(Model model, HttpSession session, HttpServletRequest request,
                               @Valid SignUpForm form, Errors errors) {
        if (UserPrincipal.getPrincipal(session) != null)
            return getHomeRedirect();

        if (errors.hasErrors()) {
            form.setPassword(null);
            form.setRepeatPassword(null);
            return new ModelAndView("login");
        }

        UserPrincipal principal = new UserPrincipal();
        principal.setUsername(form.getUsername());
        try {
            authenticationService.saveUser(principal, form.getPassword());
        }
        catch (ConstraintViolationException e) {
            form.setPassword(null);
            form.setRepeatPassword(null);
            model.addAttribute("validationErrors", e.getConstraintViolations());
            return new ModelAndView("signup");
        }

        UserPrincipal.setPrincipal(session, principal);
        request.changeSessionId();
        return getHomeRedirect();
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpSession session) {
        session.invalidate();
        return getHomeRedirect();
    }


    private ModelAndView getHomeRedirect() {
        return new ModelAndView(new RedirectView("/", true, false));
    }


    public static class LogInForm {

        private String username;
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

    public static class SignUpForm {

        private String username;
        private String password;
        private String repeatPassword;

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

        public String getRepeatPassword() {
            return repeatPassword;
        }

        public void setRepeatPassword(String repeatPassword) {
            this.repeatPassword = repeatPassword;
        }
    }
}
