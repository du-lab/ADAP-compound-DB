package org.dulab.site.controllers;

import org.dulab.models.UserPrincipal;
import org.dulab.site.services.AuthenticationService;
import org.dulab.site.services.DefaultAuthenticationService;
import org.dulab.site.services.SubmissionService;
import org.dulab.site.services.SubmissionServiceImpl;
import org.dulab.validation.ContainsUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

@Controller
public class AccountController {

    private final AuthenticationService authenticationService;

    private final SubmissionService submissionService;

    @Autowired
    public AccountController(AuthenticationService authenticationService,
                             SubmissionService submissionService) {

        this.authenticationService = authenticationService;
        this.submissionService = submissionService;
    }


    @RequestMapping(value = "account/", method = RequestMethod.GET)
    public String view(HttpSession session, Model model) {
        UserPrincipal user = UserPrincipal.from(session);

        if (user == null)
            return "redirect:/login/";

        model.addAttribute("user", user);
        model.addAttribute("submissions", submissionService.getSubmissionsByUserId(user.getId()));

//        user = authenticationService.findUser(user.getId());

        return "account/view";
    }
}
