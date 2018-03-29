package org.dulab.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

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
}
