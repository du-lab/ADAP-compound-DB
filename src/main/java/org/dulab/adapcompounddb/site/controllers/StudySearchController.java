package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.dto.SubmissionMatchDTO;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.controllers.forms.FilterForm;
import org.dulab.adapcompounddb.site.services.StudySearchService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class StudySearchController extends BaseController {

    private final StudySearchService studySearchService;
    private final SubmissionService submissionService;

    @Autowired
    public StudySearchController(StudySearchService studySearchService, SubmissionService submissionService) {
        this.studySearchService = studySearchService;
        this.submissionService = submissionService;
    }

    @RequestMapping(value = "/file/study_search/", method = RequestMethod.GET)
    public String groupSearch(final HttpSession session, final Model model) {

        final Submission submission = Submission.from(session);
        if (submission == null) {
            return "redirect:/file/upload/";
        }

        List<SubmissionMatchDTO> submissionMatches =
                studySearchService.studySearch(this.getCurrentUserPrincipal(), submission);
        model.addAttribute("match_submissions", submissionMatches);
        return "file/study_search";
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/study_search/")
    public String groupSearch(Model model, @PathVariable("submissionId") long submissionId) {

        Submission submission = submissionService.findSubmission(submissionId);
        List<SubmissionMatchDTO> submissionMatches =
                studySearchService.studySearch(this.getCurrentUserPrincipal(), submission);
        model.addAttribute("match_submissions", submissionMatches);
        return "file/study_search";
    }
}
