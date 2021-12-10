package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.dto.SubmissionDTO;
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
import java.util.stream.Collectors;

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

        Map<Long, Boolean> idToIsLibraryMap = submissionService.getIdToIsLibraryMap(submissions);
        Map<Long, Boolean> idToIsInHouseLibraryMap = submissionService.getIdToIsInHouseLibraryMap(submissions);

        Map<Long, List<ChromatographyType>> submissionIdToChromatographyListMap =
                submissionService.findChromatographyTypes(submissions);

        List<SubmissionDTO> submissionDTOs = submissions.stream()
                .map(s -> new SubmissionDTO(s, idToIsLibraryMap.get(s.getId()), idToIsInHouseLibraryMap.get(s.getId()),
                        false))
                .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("submissionList", submissionDTOs);
        model.addAttribute("submissionIdToChromatographyListMap", submissionIdToChromatographyListMap);
        return "account/view";
    }
}
