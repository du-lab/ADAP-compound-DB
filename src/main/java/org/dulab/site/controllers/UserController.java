package org.dulab.site.controllers;

import org.dulab.site.models.UserPrincipal;
import org.dulab.site.authentication.DefaultUserPrincipalManager;
import org.dulab.site.authentication.UserPrincipalManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserController {

    private UserPrincipalManager userManager;

    public UserController() {
        userManager = new DefaultUserPrincipalManager();
    }
//
//    @RequestMapping(value = {"user/login", "user/signup"}, method = RequestMethod.GET)
//    public String home(Model model) {
//        model.addAttribute("logInForm", new LogInForm());
//        model.addAttribute("signUpForm", new SignUpForm());
//        return "user/login";
//    }

//    @RequestMapping(value = "user/signup", method = RequestMethod.POST)
//    public View signUp(SignUpForm form) {
//
//    }

//    @RequestMapping(value = "user/add", method = RequestMethod.GET)
//    public String createUser(Model model) {
//        model.addAttribute("userForm", new SignUpForm());
//        return "user/add";
//    }

//    @RequestMapping(value = "user/edit/{userId}", method = RequestMethod.GET)
//    public String editUser(Model model, @PathVariable("userId") long userId) {
//        SignUpForm form = new SignUpForm();
//        // Set fields of SignUpForm
//        model.addAttribute("userForm", form);
//        return "user/edit";
//    }

    @RequestMapping(value = "user/list", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("users", userManager.getUsers());
        return "user/list";
    }

    @RequestMapping(value = "user/", method = RequestMethod.POST)
    public View add() {
        UserPrincipal user = new UserPrincipal();
        userManager.saveUser(user);
        return new RedirectView("/", true, false);
    }
}
