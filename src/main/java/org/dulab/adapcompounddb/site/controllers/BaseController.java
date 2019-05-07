package org.dulab.adapcompounddb.site.controllers;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public abstract class BaseController {

    protected static final String ACCESS_DENIED_MESSAGE = "Sorry you do not have access to this page";
    protected static final String SESSION_ATTRIBUTE_KEY = "currentSubmission";
    private static final String SESSION_CACHE_KEY = "cacheKey";
    private static final int MAX_CACHE_SIZE = 5;

    @Autowired
    UserPrincipalService userPrincipalService;

    public SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    public UserPrincipal getCurrentUserPrincipal() {
        return userPrincipalService.findUserByUsername(getCurrentUsername());
    }

    public Authentication getAuthentication() {
        return getSecurityContext().getAuthentication();
    }

    public String getCurrentUsername() {
        String username = null;
        final Object user = getAuthentication().getPrincipal();
        if (user instanceof User && user != null) {
            username = ((User) user).getUsername();
        }
        return username;
    }

    public boolean isAuthenticated() {
        return getCurrentUsername() != null;
    }

    public Submission getSubmissionFromSession(final HttpSession session) {
        return session == null ? null : (Submission) session.getAttribute(SESSION_ATTRIBUTE_KEY);
    }

    public void assign(final HttpSession session, final Submission submission) {
        session.setAttribute(SESSION_ATTRIBUTE_KEY, submission);
    }

    public void clearSession(final HttpSession session) {
        session.removeAttribute(SESSION_ATTRIBUTE_KEY);
        final Map<SearchParams, List<SpectrumMatch>> cache = getSearchResultCache(session);
        SearchParams searchParams = null;
        for(final SearchParams params: cache.keySet()) {
            if(params.getSubmissionId() == 0) {
                searchParams  = params;
                break;
            }
        }
        if(searchParams != null) {
            cache.remove(searchParams);
        }
    }

    public void addSearchResultsToCache(final HttpSession session, final SearchParams searchParams, final List<SpectrumMatch> matches) {
        Map<SearchParams, List<SpectrumMatch>> cachedSpectraSearchResults = getSearchResultCache(session);
        if(cachedSpectraSearchResults == null) {
            cachedSpectraSearchResults = new Hashtable<>();
            session.setAttribute(SESSION_CACHE_KEY, cachedSpectraSearchResults);
        }
        if(cachedSpectraSearchResults.size() > MAX_CACHE_SIZE) {
            cachedSpectraSearchResults.remove(cachedSpectraSearchResults.keySet().stream().findAny().get());
        }
        cachedSpectraSearchResults.put(searchParams, matches);
    }

    public Map<SearchParams, List<SpectrumMatch>> getSearchResultCache(final HttpSession session) {
        return session == null ? null : (Map<SearchParams, List<SpectrumMatch>>) session.getAttribute(SESSION_CACHE_KEY);
    }
}
