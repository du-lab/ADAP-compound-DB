package org.dulab.adapcompounddb.site.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.Statistics;
import org.dulab.adapcompounddb.models.enums.UserRole;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.StatisticsService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    private final StatisticsService statisticsService;
    private final UserPrincipalService userPrincipalService;
    private final SpectrumService spectrumService;

    private final SubmissionService submissionService;
//    private final Progress progress;

    @Autowired
    public AdminController(StatisticsService statisticsService,
                           UserPrincipalService userPrincipalService,
                           SpectrumService spectrumService,
                           SubmissionService submissionSerivce) {

        this.statisticsService = statisticsService;
        this.userPrincipalService = userPrincipalService;
        this.spectrumService = spectrumService;
        this.submissionService=submissionSerivce;

//        progress = new Progress();
    }

    @RequestMapping(value = "/admin/", method = RequestMethod.GET)
    public String admin(final Model model) {

        final Map<ChromatographyType, Statistics> statisticsMap = new TreeMap<>();
        for (final ChromatographyType type : ChromatographyType.values()) {
            statisticsMap.put(type, statisticsService.getStatistics(type));
        }

        model.addAttribute("statistics", statisticsMap);
        model.addAttribute("availableUserRoles", UserRole.values());
        model.addAttribute("users", userPrincipalService.findAllUsers());
        return "admin/admin";
    }


    @RequestMapping(value = "/admin/set/submission/{submissionId:\\d+}/reference/{value}", method = RequestMethod.GET)
    public String setSubmissionReference(@PathVariable("submissionId") long submissionId,
                                         @PathVariable("value") boolean value) throws JsonProcessingException {
        spectrumService.updateReferenceBySubmissionId(submissionId, value);
        submissionService.updateReferenceBySubmissionId(submissionId, value);
        return "redirect:/admin/";
    }

    @RequestMapping(value = "/admin/set/submission/{submissionId:\\d+}/clusterable/{value}", method = RequestMethod.GET)
    public String setSubmissionClusterable(@PathVariable("submissionId") long submissionId,
                                           @PathVariable("value") boolean value) throws JsonProcessingException {
        spectrumService.updateClusterableBySubmissionId(submissionId, value);
        return "redirect:/admin/";
    }

//    public static class Progress implements Serializable {
//
//        private final static long serialVersionUID = 1L;
//
//        private Integer value;
//
//        public Integer getValue() {
//            return value;
//        }
//
//        public void setValue(final Integer value) {
//            this.value = value;
//        }
//    }

    public static void main (String[] args) throws IOException {
        String version = AdminController.class.getPackage().getImplementationVersion();
        if (version == null) {
            Properties props = new Properties();
            InputStream in = AdminController.class.getResourceAsStream("/META-INF/maven/org.dulab/adap-compound-db/pom.properties");
            props.load(in);
            in.close();
            version = props.getProperty("version", "unknown");
        }
        System.out.println("Version: " + version);
    }
}
