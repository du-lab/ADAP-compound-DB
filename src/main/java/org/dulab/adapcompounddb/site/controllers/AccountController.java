package org.dulab.adapcompounddb.site.controllers;

import javax.servlet.http.HttpSession;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.AuthenticationService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AccountController {

    private final AuthenticationService authenticationService;

    private final SubmissionService submissionService;

    @Autowired
    private UserPrincipalService userPrincipalService;

    @Autowired
    public AccountController(AuthenticationService authenticationService,
                             SubmissionService submissionService) {

        this.authenticationService = authenticationService;
        this.submissionService = submissionService;
    }


    @RequestMapping(value = "account/", method = RequestMethod.GET)
    public String view(HttpSession session, Model model) {
        UserPrincipal user = userPrincipalService.getUerByUsername(((User)
        		SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
//
//        if (user == null)
//            return "redirect:/login/";

        model.addAttribute("user", user);
        model.addAttribute("submissionList", submissionService.getSubmissionsByUserId(user.getId()));

        return "account/view";
    }
}
