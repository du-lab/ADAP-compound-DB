package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.controllers.forms.FilterForm;
import org.dulab.adapcompounddb.site.services.StudySearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class StudySearchController {
    private StudySearchService studySearchService;

    public StudySearchController(StudySearchService studySearchService) {
        this.studySearchService = studySearchService;
    }

    @RequestMapping(value = "/file/study_search/", method = RequestMethod.GET)
    public String groupSearch(final HttpSession session) {

        final Submission submission = Submission.from(session);
        if (submission == null) {
            return "redirect:/file/upload/";
        }
        studySearchService.studySearch(submission);

        return "file/study_search";
    }
}
