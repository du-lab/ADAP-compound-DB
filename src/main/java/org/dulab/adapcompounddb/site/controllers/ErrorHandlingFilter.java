package org.dulab.adapcompounddb.site.controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.springframework.web.util.NestedServletException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@WebFilter(
        filterName = "errorHandling",
        urlPatterns = "/*")
public class ErrorHandlingFilter implements Filter {

    private static final Logger LOG = LogManager.getLogger(ErrorHandlingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            chain.doFilter(request, response);
        } catch (NestedServletException e) {
            Throwable t = e.getCause();
            boolean withStackTrance = !(t instanceof EmptySearchResultException);
            processError(request, response, t, withStackTrance);

        } catch (Exception e) {
            processError(request, response, e, true);
        }
    }

    private void processError(ServletRequest request, ServletResponse response, Throwable t, boolean withStackTrace)
            throws IOException, ServletException {

        String errorMessage = (t != null) ? t.getMessage() : "Unknown error";
        String errorUrl = String.format("/error?errorMsg=%s", URLEncoder.encode(errorMessage, "UTF-8"));
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(errorUrl);
        requestDispatcher.forward(request, response);
        LOG.error(String.format("(%s): %s",
                        request instanceof HttpServletRequest ? ((HttpServletRequest) request).getRequestURI() : "",
                        errorMessage),
                (withStackTrace && t != null) ? t : null);
    }

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void destroy() {
    }
}
