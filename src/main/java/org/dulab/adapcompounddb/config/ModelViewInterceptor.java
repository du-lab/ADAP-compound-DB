package org.dulab.adapcompounddb.config;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModelViewInterceptor implements HandlerInterceptor {

    private boolean INTEGRATION_TEST;

    public ModelViewInterceptor(boolean INTEGRATION_TEST) {
        this.INTEGRATION_TEST = INTEGRATION_TEST;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            modelAndView.addObject("integTest", INTEGRATION_TEST);
        }
    }
}
