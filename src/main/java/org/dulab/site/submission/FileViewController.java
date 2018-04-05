package org.dulab.site.submission;

import org.dulab.site.models.Submission;
import org.dulab.site.validation.ContainsSubmission;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class FileViewController {

    @RequestMapping(value = "/file/view", method = RequestMethod.GET)
    public String view(@ContainsSubmission HttpSession session) {
        return "file/view";
    }

    @RequestMapping(value = "/file/raw/view", method = RequestMethod.GET)
    public void rawView(@ContainsSubmission HttpSession session, HttpServletResponse response) throws IOException {

        Submission submission = Submission.getSubmission(session);
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "inline; filename=\"" + submission.getFilename() + "\"");
        response.getOutputStream().write(submission.getFile());
    }

    @RequestMapping(value = "/file/raw/download", method = RequestMethod.GET)
    public void rawDownload(@ContainsSubmission HttpSession session, HttpServletResponse response) throws IOException {

        Submission submission = Submission.getSubmission(session);
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + submission.getFilename() + "\"");
        response.getOutputStream().write(submission.getFile());
    }
}
