package org.dulab.adapcompounddb.site.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
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
        } catch (Exception e) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(
                    String.format("/error?errorMsg=%s",
                            URLEncoder.encode(e.getMessage(), "UTF-8")));
            requestDispatcher.forward(request, response);
            LOG.error(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void destroy() {
    }
}
