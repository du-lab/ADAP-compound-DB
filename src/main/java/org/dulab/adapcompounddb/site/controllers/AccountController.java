package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.dto.SubmissionDTO;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.controllers.forms.CompoundSearchForm;
import org.dulab.adapcompounddb.site.controllers.forms.FilterForm;
import org.dulab.adapcompounddb.site.controllers.forms.FilterOptions;
import org.dulab.adapcompounddb.site.controllers.utils.ConversionsUtils;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.dulab.adapcompounddb.site.controllers.utils.ControllerUtils.INDIVIDUAL_SEARCH_PARAMETERS_COOKIE_NAME;

@Controller
public class AccountController extends BaseController {
    private static final double MEMORY_PER_PEAK = 1.3e-7; //in GB
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

        List<SubmissionDTO> submissionDTOs = submissions.stream()
                .map(s -> new SubmissionDTO(s,
                        s.getIsReference(),
                        s.isInHouseReference(),
                        false))
                .collect(Collectors.toList());

        int peakCapacity = user.getPeakCapacity();
        double maxDiskSpace = MEMORY_PER_PEAK * peakCapacity;
        double currentDiskSpace = submissionService.getPeakDiskSpaceByUser(user.getUsername());

        model.addAttribute(("currentDiskSpace"), currentDiskSpace);
        model.addAttribute(("maxDiskSpace"), maxDiskSpace);
        model.addAttribute("user", user);
        model.addAttribute("submissionList", submissionDTOs);
        model.addAttribute("submissionIdToChromatographyListMap", submissionIdToChromatographyListMap);
        model.addAttribute("filterForm",new FilterForm());
        return "account/view";
    }
    @RequestMapping(value = "/account/saveparameters", method = RequestMethod.POST)
    public String saveParameters(Model model,
                                 @RequestParam ("scoreThreshold") Integer scoreThreshold,
                                 @RequestParam ("retentionIndexTolerance") Double retentionIndexTolerance,
                                 @RequestParam ("retentionIndexMatch") SearchParameters.RetIndexMatchType retentionIndexMatch,
                                 @RequestParam ("mzTolerance") Double mzTolerance,
                                 @RequestParam ("mzToleranceType") SearchParameters.MzToleranceType mzToleranceType,
                                 @RequestParam ("limit") Integer limit) {
        UserPrincipal user = getCurrentUserPrincipal();
        List<Submission> submissions = submissionService.findSubmissionsWithTagsByUserId(user.getId());
        Map<Long, List<ChromatographyType>> submissionIdToChromatographyListMap =
                submissionService.findChromatographyTypes(submissions);
        List<SubmissionDTO> submissionDTOs = submissions.stream()
                .map(s -> new SubmissionDTO(s,
                        s.getIsReference(),
                        s.isInHouseReference(),
                        false))
                .collect(Collectors.toList());
        int peakCapacity = user.getPeakCapacity();
        double maxDiskSpace = MEMORY_PER_PEAK * peakCapacity;
        double currentDiskSpace = submissionService.getPeakDiskSpaceByUser(user.getUsername());
        model.addAttribute(("currentDiskSpace"), currentDiskSpace);
        model.addAttribute(("maxDiskSpace"), maxDiskSpace);
        model.addAttribute("user", user);
        model.addAttribute("submissionList", submissionDTOs);
        model.addAttribute("submissionIdToChromatographyListMap", submissionIdToChromatographyListMap);
        model.addAttribute("filterForm",new FilterForm());
        return "account/view";
    }
}
