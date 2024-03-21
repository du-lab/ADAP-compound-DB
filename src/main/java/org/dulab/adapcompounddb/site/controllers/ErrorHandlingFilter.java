package org.dulab.adapcompounddb.site.controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.NestedServletException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Component
@WebFilter(
        filterName = "errorHandling",
        urlPatterns = "/*")
public class ErrorHandlingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandlingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            chain.doFilter(request, response);
        } catch (NestedServletException e) {
            Throwable t = e.getCause();
            boolean withStackTrace = !(t instanceof EmptySearchResultException);
            processError(request, response, t, withStackTrace);

        } catch (Exception e) {
            processError(request, response, e, true);
        }
    }

    private void processError(ServletRequest request, ServletResponse response, Throwable t, boolean withStackTrace)
            throws IOException, ServletException {

        String errorMessage = (t != null) ? t.getMessage() : "Unknown error";
        LOG.error(String.format("(%s): %s",
                        request instanceof HttpServletRequest ? ((HttpServletRequest) request).getRequestURI() : "",
                        errorMessage),
                (withStackTrace) ? t : null);

        String errorUrl = String.format("/error?errorMsg=%s",
                errorMessage != null ? URLEncoder.encode(errorMessage, "UTF-8") : null);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(errorUrl);
        requestDispatcher.forward(request, response);
    }

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void destroy() {
    }
}
