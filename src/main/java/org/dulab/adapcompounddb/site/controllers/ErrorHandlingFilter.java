package org.dulab.adapcompounddb.site.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(
        filterName = "errorHandling",
        urlPatterns = "/*")
public class ErrorHandlingFilter implements Filter {

    private static final Logger LOG = LogManager.getLogger();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException {

        try {
            chain.doFilter(request, response);
        }
        catch (Exception e) {
        	e.printStackTrace();
            if (response instanceof HttpServletResponse)
                ((HttpServletResponse) response).sendRedirect(String.format("/error?errorMsg=\"%s\"", e.getMessage()));
            LOG.error(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void init(FilterConfig config) {}

    @Override
    public void destroy() {}
}
