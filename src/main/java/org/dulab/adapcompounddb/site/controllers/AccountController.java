package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.dto.SearchParametersDTO;
import org.dulab.adapcompounddb.models.dto.SubmissionDTO;
import org.dulab.adapcompounddb.models.entities.SearchTask;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.controllers.forms.FilterForm;
import org.dulab.adapcompounddb.site.controllers.forms.OrganizationForm;
import org.dulab.adapcompounddb.site.services.SearchTaskService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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

    private Executor threadPoolTaskExecutor;


    @Autowired
    public AccountController(SubmissionService submissionService, UserPrincipalService userPrincipalService,
                             SearchTaskService searchTaskService, Executor threadPoolTaskExecutor) {
        this.submissionService = submissionService;
        this.userPrincipalService = userPrincipalService;
        this.searchTaskService = searchTaskService;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
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
        model.addAttribute("selectedTab","studies");
        model.addAttribute("organizationForm",new OrganizationForm());
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
        model.addAttribute("selectedTab","parameters");
        model.addAttribute("organizationForm",new OrganizationForm());
        return "account/view";
    }
    @RequestMapping(value = "account/addUserToOrganization", method = RequestMethod.POST)
    public String inviteUsersToOrganization(Model model,
                                            @RequestParam("selectedUsers") List<Long> selectedUsers) {

        UserPrincipal user = getCurrentUserPrincipal();
        String errorMessage = "";
        if (user.isOrganization()) {
            try {
                userPrincipalService.sendInviteToUser(user, selectedUsers);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                model.addAttribute("errorMessage", e.getMessage());
            }
        } else {
            model.addAttribute("errorMessage", "You are not allowed to perform this action.");
        }
        populateViewModel(model, user);
        if (errorMessage.length() == 0)
            model.addAttribute("successMessage", "Invitation sent to user.");
        model.addAttribute("selectedTab","organization");
        return "account/view";
    }

    @RequestMapping(value = "account/convertToOrganization", method = RequestMethod.GET)
    public String convertExistingAccountToOrganization(Model model) {
        UserPrincipal user = getCurrentUserPrincipal();
        String errorMessage = "";
        if (user != null) {
            user.setOrganization(true);
            userPrincipalService.saveUserPrincipal(user);
            populateViewModel(model, user);
        } else {
            errorMessage = "You are not allowed to perform this action.";
            model.addAttribute("errorMessage", errorMessage);
        }
        model.addAttribute("selectedTab","organization");
        if (errorMessage.length() == 0)
            model.addAttribute("successMessage", "Account converted to organization.");
        return "account/view";
    }

    @RequestMapping(value = "account/fetchUserNamesForOrganization", method = RequestMethod.POST)
    public String fetchUsernamesForOrganization(Model model,
                                                @RequestParam ("username") String username) {

        UserPrincipal user = getCurrentUserPrincipal();
        if (user.isOrganization()) {
            try {
                List<UserPrincipal> userPrincipalList = userPrincipalService.fetchUsernamesForOrganization(username, user);
                if (userPrincipalList.isEmpty())
                    throw new EmptySearchResultException("No valid users found with \"" + username + "\"");
                model.addAttribute("searchMembersList", userPrincipalList);
            } catch (Exception e) {
                model.addAttribute("errorMessage", e.getMessage());
            }
        } else {
            model.addAttribute("errorMessage", "You are not allowed to perform this action.");
        }
        populateViewModel(model, user);
        model.addAttribute("selectedTab","organization");
        return "account/view";
    }

    @RequestMapping(value = "account/organization/{username:\\w+}/delete/", method = RequestMethod.GET)
    public String deleteUserFromOrganization(Model model,
                                             @PathVariable("username") String username) {
        UserPrincipal user = getCurrentUserPrincipal();
        String errorMessage = "";
        if (user.isOrganization() || user.getUsername().equals(username)) { //current user is organization account OR current user is removing him/her self
            try {
                user = userPrincipalService.deleteUserFromOrganization(username, user);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                model.addAttribute("errorMessage", e.getMessage());
            }
        } else {
            model.addAttribute("errorMessage", "You are not allowed to perform this action.");
        }
        populateViewModel(model, user);
        model.addAttribute("selectedTab","organization");
        if (errorMessage.length() == 0)
            model.addAttribute("successMessage", "User deleted from organization.");
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

    private void populateViewModel(Model model, UserPrincipal user) {
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
        model.addAttribute("searchParameters",user.getSearchParametersDTO());
        model.addAttribute(("currentDiskSpace"), currentDiskSpace);
        model.addAttribute(("maxDiskSpace"), maxDiskSpace);
        model.addAttribute("user", user);
        model.addAttribute("submissionList", submissionDTOs);
        model.addAttribute("submissionIdToChromatographyListMap", submissionIdToChromatographyListMap);
        model.addAttribute("filterForm",new FilterForm());
        model.addAttribute("organizationForm",new OrganizationForm());
    }
}
