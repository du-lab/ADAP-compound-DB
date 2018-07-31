package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AccountController extends BaseController {

	private final SubmissionService submissionService;

    @Autowired
    public AccountController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "account/", method = RequestMethod.GET)
    public String view(Model model) {

        UserPrincipal user = getCurrentUserPrincipal();

        model.addAttribute("user", user);
        model.addAttribute("submissionList", submissionService.findSubmissionsWithTagsByUserId(user.getId()));

        return "account/view";
    }
}
