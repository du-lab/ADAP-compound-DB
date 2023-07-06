package org.dulab.adapcompounddb.config;

import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.utils.BreadCrumbs;
import org.dulab.adapcompounddb.utils.JSPPageNames;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModelViewInterceptor implements HandlerInterceptor {

    private boolean INTEGRATION_TEST;

    public ModelViewInterceptor(boolean INTEGRATION_TEST) {
        this.INTEGRATION_TEST = INTEGRATION_TEST;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            String Pathinfo = request.getPathInfo();
            String x = request.getRequestURL().toString();
            String requestURI = request.getRequestURI();
            modelAndView.addObject("integTest", INTEGRATION_TEST);
            String s = modelAndView.getViewName();
            System.out.println("ViewName : " +s);
            modelAndView.addObject("breadcrumbs", generateBreadcrumbs(modelAndView, request));
        }
    }

    private List<BreadCrumbs> generateBreadcrumbs(ModelAndView modelAndView, HttpServletRequest request) {
        String pageName = modelAndView.getViewName();
        List<BreadCrumbs> breadcrumbs = new ArrayList<>();
        ArrayList<BreadCrumbs> prev = (ArrayList<BreadCrumbs>) request.getSession().getAttribute("breadcrumbs");
        if (JSPPageNames.ALL_LIBRARIES.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Libraries", "/libraries/"));
        } else if (JSPPageNames.ALL_CLUSTERS.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Libraries", "/libraries/"));
            breadcrumbs.add(new BreadCrumbs("Consensus Spectra", "/all_clusters/"));
        } else if (JSPPageNames.PUBLIC_STUDIES.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Libraries", "/libraries/"));
            breadcrumbs.add(new BreadCrumbs("Public Studies", "/public_studies/"));
        } else if (JSPPageNames.ABOUT.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("About", "/about/"));
        } else if (JSPPageNames.DOWNLOADS.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Downloads", "/downloads/"));
        } else if (JSPPageNames.STUDY_DISTRIBUTIONS.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Distributions", "/study_distributions/"));
        } else if (JSPPageNames.INDEX.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Home", "/"));
        } else if (JSPPageNames.COMPOUND_SEARCH.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Manual Search", "/compound/search/"));
        } else if (JSPPageNames.COMPOUND_SEARCHRESULTS.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Manual Search", "/compound/search/"));
            breadcrumbs.add(new BreadCrumbs("Search Results", "/compound/search/"));
        } else if (JSPPageNames.ACCOUNT.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Account", "/account/"));
        } else if (JSPPageNames.UPLOAD.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Files Uploads", "/file/upload/"));
        } else if (JSPPageNames.METADATA.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Files Uploads", "/file/upload/"));
            breadcrumbs.add(new BreadCrumbs("Add Metadata", "/submission/metadata"));
        } else if (JSPPageNames.REDIRECT_FILE.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Upload Files", "/file/upload/"));
            breadcrumbs.add(new BreadCrumbs("Add Metadata", "/submission/metadata"));
        } else if (JSPPageNames.GROUP_SEARCH_PARAMETERS.equals(pageName)) {
            if (prev != null && prev.size() > 0) {
                if (Objects.equals(prev.get(0).getLabel(), "Upload Files")) {
                    breadcrumbs.add(new BreadCrumbs("Upload Files", "/file/upload/"));
                    breadcrumbs.add(new BreadCrumbs("Add Metadata", "/submission/metadata"));
                    breadcrumbs.add(new BreadCrumbs("Data", "/file/"));
                    breadcrumbs.add(new BreadCrumbs("Search Parameters", "/group_search/parameters"));
                }
            }
        } else if (JSPPageNames.GROUP_SEARCH.equals(pageName)) {
            breadcrumbs.add(new BreadCrumbs("Upload Files", "/file/upload/"));
            breadcrumbs.add(new BreadCrumbs("Add Metadata", "/submission/metadata"));
            breadcrumbs.add(new BreadCrumbs("Data", "/file/"));
            breadcrumbs.add(new BreadCrumbs("Search Parameters", "/group_search/parameters"));
            breadcrumbs.add(new BreadCrumbs("Search Results", "/group_search/"));
        } else if (JSPPageNames.SUBMISSIONS.equals(pageName)) {
            String submissionName = "Data";
            String submissionType = "Studies";
            if (prev != null && prev.size() > 0) {
                breadcrumbs.add(prev.get(0));
                if (prev.size() > 1 && (!Objects.equals(prev.get(0).getLabel(), "Account") && !Objects.equals(prev.get(0).getLabel(), "Library")))
                    breadcrumbs.add(prev.get(1));
                if (!modelAndView.isEmpty()) {
                    Map<String, Object> model = modelAndView.getModel();
                    Submission submission = (Submission) model.get("submission");
                    submissionName = submission.getName();
                    submissionType = submission.getIsReference() ?
                            "Libraries" : "Studies";
                }
                if (Objects.equals(prev.get(0).getLabel(), "Account")) {
                    breadcrumbs.add(new BreadCrumbs(submissionType, "/account/"));
                }
                if (Objects.equals(prev.get(0).getLabel(), "Libraries")) {

                }
                if (Objects.equals(prev.get(0).getLabel(), "Upload Files")) {
                    breadcrumbs.clear();
                    breadcrumbs.addAll(prev);
                }
            }
            breadcrumbs.add(new BreadCrumbs(submissionName, request.getRequestURI()));
        } else if (JSPPageNames.SPECTRUM.equals(pageName)) {
            if (prev != null && prev.size() > 0) {
                if (Objects.equals(prev.get(0).getLabel(), "Upload Files")) {
                    breadcrumbs.add(new BreadCrumbs("Upload Files", "/file/upload/"));
                    breadcrumbs.add(new BreadCrumbs("Add Metadata", "/submission/metadata"));
                    breadcrumbs.add(new BreadCrumbs("Data", "/file/"));
                    breadcrumbs.add(new BreadCrumbs("Search Parameters", "/group_search/parameters"));
                    breadcrumbs.add(new BreadCrumbs("Search Results", "/group_search/"));
                } else {
                    breadcrumbs.addAll(prev);
                }
            }
            breadcrumbs.add(new BreadCrumbs("Library Spectrum", request.getRequestURI()));
        } else if (JSPPageNames.SPECTRUM_INFO.equals(pageName)) {
            if (prev != null) {
                breadcrumbs.addAll(prev);
            }
        }
        request.getSession().setAttribute("breadcrumbs", breadcrumbs);
        return breadcrumbs;
    }
}
