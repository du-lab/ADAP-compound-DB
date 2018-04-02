package org.dulab.site.controllers;

import org.dulab.site.models.User;
import org.dulab.site.services.DefaultUserManagerService;
import org.dulab.site.services.UserManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserController {

    private UserManagerService userManager;

    public UserController() {
        userManager = new DefaultUserManagerService();
    }

    @RequestMapping(value = "user/login", method = RequestMethod.GET)
    public String home() {
        return "user/login";
    }

    @RequestMapping(value = "user/add", method = RequestMethod.GET)
    public String createUser(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "user/add";
    }

    @RequestMapping(value = "user/edit/{userId}", method = RequestMethod.GET)
    public String editUser(Model model, @PathVariable("userId") long userId) {
        UserForm form = new UserForm();
        // Set fields of UserForm
        model.addAttribute("userForm", form);
        return "user/edit";
    }

    @RequestMapping(value = "user/list", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("users", userManager.getUsers());
        return "user/list";
    }

    @RequestMapping(value = "user/", method = RequestMethod.POST)
    public View add() {
        User user = new User();
        userManager.saveUser(user);
        return new RedirectView("/", true, false);
    }
}
