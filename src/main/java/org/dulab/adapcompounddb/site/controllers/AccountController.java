package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.dto.SearchParametersDTO;
import org.dulab.adapcompounddb.models.dto.SubmissionDTO;
import org.dulab.adapcompounddb.models.entities.SearchTask;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.controllers.forms.FilterForm;
import org.dulab.adapcompounddb.site.services.SearchTaskService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Controller
public class AccountController extends BaseController {
    private static final double MEMORY_PER_PEAK = 1.3e-7; //in GB
    private final SubmissionService submissionService;
    private final UserPrincipalService userPrincipalService;
    private final SearchTaskService searchTaskService;

    @Autowired
    private Executor threadPoolTaskExecutor;


    @Autowired
    public AccountController(SubmissionService submissionService, UserPrincipalService userPrincipalService, SearchTaskService searchTaskService) {
        this.submissionService = submissionService;
        this.userPrincipalService = userPrincipalService;
        this.searchTaskService = searchTaskService;
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
        double currentDiskSpace = submissionService.getPeakDiskSpaceByUser(user);
        List<SearchTask> searchTaskList = searchTaskService.findSearchTaskByUser(user);
        model.addAttribute("searchParameters",user.getSearchParametersDTO());
        model.addAttribute(("currentDiskSpace"), currentDiskSpace);
        model.addAttribute(("maxDiskSpace"), maxDiskSpace);
        model.addAttribute("user", user);
        model.addAttribute("submissionList", submissionDTOs);
        model.addAttribute("searchTaskList", searchTaskList);
        model.addAttribute("submissionIdToChromatographyListMap", submissionIdToChromatographyListMap);
        model.addAttribute("filterForm",new FilterForm());
        return "account/view";
    }
    @RequestMapping(value = "/account/saveparameters", method = RequestMethod.POST)
    public String saveParameters(Model model,
                                 @RequestParam ("scoreThreshold") Integer scoreThreshold,
                                 @RequestParam ("retentionIndexTolerance") Integer retentionIndexTolerance,
                                 @RequestParam ("retentionIndexMatch") SearchParameters.RetIndexMatchType retentionIndexMatch,
                                 @RequestParam ("mzTolerance") Double mzTolerance,
                                 @RequestParam ("mzToleranceType") SearchParameters.MzToleranceType mzToleranceType,
                                 @RequestParam ("limit") Integer limit) {
        UserPrincipal user = getCurrentUserPrincipal();
        SearchParametersDTO searchParameters = new SearchParametersDTO(scoreThreshold,retentionIndexTolerance,
                retentionIndexMatch,mzTolerance,limit,mzToleranceType,false);
        SearchParametersDTO searchParametersDTO = userPrincipalService.updateSearchParameters(searchParameters, user);
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
        double currentDiskSpace = submissionService.getPeakDiskSpaceByUser(user);
        searchParametersDTO.checkCustomParameters();
        model.addAttribute("searchParameters",searchParametersDTO);
        model.addAttribute(("currentDiskSpace"), currentDiskSpace);
        model.addAttribute(("maxDiskSpace"), maxDiskSpace);
        model.addAttribute("user", user);
        model.addAttribute("submissionList", submissionDTOs);
        model.addAttribute("submissionIdToChromatographyListMap", submissionIdToChromatographyListMap);
        model.addAttribute("filterForm",new FilterForm());
        return "account/view";
    }

    @RequestMapping(value = "/account/getSearchTaskStatus", method = RequestMethod.GET)
    @ResponseBody
    public String getSearchTaskStatus() {
        if (threadPoolTaskExecutor instanceof ThreadPoolTaskExecutor) {
            ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) threadPoolTaskExecutor;
            final int activeCount = executor.getActiveCount();
            final int poolSize = executor.getThreadPoolExecutor().getCorePoolSize();
            final int queuedTaskSize = executor.getThreadPoolExecutor().getQueue().size();
            return poolSize+"/"+activeCount+"/"+queuedTaskSize;
        }
        return "0/0/0";
    }
}
