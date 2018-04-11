package org.dulab.site.controllers;

import org.dulab.models.UserPrincipal;
import org.dulab.site.services.AuthenticationService;
import org.dulab.site.services.DefaultAuthenticationService;
import org.dulab.validation.ContainsUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

@Controller
public class AccountController {

    private AuthenticationService authenticationService;

    public AccountController() {
        authenticationService = new DefaultAuthenticationService();
    }

    @RequestMapping(value = "account/", method = RequestMethod.GET)
    public String view(HttpSession session, Model model) {
        UserPrincipal user = UserPrincipal.from(session);

        if (user == null)
            return "redirect:/login/";

        user = authenticationService.findUser(user.getId());
        model.addAttribute("user", user);
        return "account/view";

        //TODO List of submissions doesn't update after deleting of a submission
    }
}
