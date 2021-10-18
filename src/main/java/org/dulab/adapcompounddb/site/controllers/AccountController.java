package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

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
        List<Submission> submissions = submissionService.findSubmissionsWithTagsByUserId(user.getId());
        Map<Long, List<ChromatographyType>> submissionIdToChromatographyListMap =
                submissionService.findChromatographyTypes(submissions);

        model.addAttribute("user", user);
        model.addAttribute("submissionList", submissions);
        model.addAttribute("submissionIdToChromatographyListMap", submissionIdToChromatographyListMap);
        return "account/view";
    }
}
