package org.dulab.adapcompounddb.site.controllers;

import javax.servlet.http.HttpSession;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AccountController extends BaseController {

	@Autowired
    private SubmissionService submissionService;

    @RequestMapping(value = "account/", method = RequestMethod.GET)
    public String view(HttpSession session, Model model) {
        UserPrincipal user = getCurrentUserPrincipal();
//
//        if (user == null)
//            return "redirect:/login/";

        model.addAttribute("user", user);
        model.addAttribute("submissionList", submissionService.getSubmissionsByUserId(user.getId()));

        return "account/view";
    }
}
