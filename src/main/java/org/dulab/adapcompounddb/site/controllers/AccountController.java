package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
        model.addAttribute("submissionList", submissionService.getSubmissionsByUserId(user.getId()));

        return "account/view";
    }
}
