package org.dulab.site.submission;

import org.dulab.site.models.Submission;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

@Controller
public class SubmissionController {

    @RequestMapping(value = "/submission", method = RequestMethod.GET)
    public ModelAndView submission(HttpSession session) {
        if (Submission.getPrincipal(session) == null)
            return new ModelAndView(new RedirectView("/submission/upload", true, false));

        return new ModelAndView("submission/view");
    }

    @RequestMapping(value = "/submission/upload", method = RequestMethod.GET)
    public ModelAndView upload(HttpSession session) {
        if (Submission.getPrincipal(session) != null)
            return new ModelAndView(new RedirectView("/submission/view", true, false));

        return new ModelAndView("submission/upload");
    }
}
