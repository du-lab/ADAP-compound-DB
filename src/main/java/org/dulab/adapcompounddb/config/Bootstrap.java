package org.dulab.adapcompounddb.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class Bootstrap implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext container) throws ServletException {

        container.getServletRegistration("default")
                .addMapping("/resources/*");

        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(ApplicationContextConfiguration.class, WebSecurityConfig.class);
        container.addListener(new ContextLoaderListener(applicationContext));

        AnnotationConfigWebApplicationContext servletContext = new AnnotationConfigWebApplicationContext();
        servletContext.register(ApplicationContextConfiguration.class, ServletContextConfiguration.class, WebSecurityConfig.class);

        ServletRegistration.Dynamic dispatcher = container.addServlet(
                "springDispatcher", new DispatcherServlet(servletContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

    }


}
