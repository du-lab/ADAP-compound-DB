package org.dulab.adapcompounddb.config;


import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

public class SitemeshConfig extends ConfigurableSiteMeshFilter {

    @Override
    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        builder.addDecoratorPath("/*", "/WEB-INF/jsp/view/decorator.jsp").addExcludedPath("/ajax/*");
//        builder.addDecoratorPath("/login/*", "/WEB-INF/jsp/view/login.jsp");
    }
}
