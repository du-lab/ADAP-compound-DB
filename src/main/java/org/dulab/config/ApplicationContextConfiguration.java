package org.dulab.config;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(
        mode = AdviceMode.PROXY,
        proxyTargetClass = false
)
@ComponentScan(
        basePackages = "org.dulab.site",
        excludeFilters = @ComponentScan.Filter(Controller.class)
)
public class ApplicationContextConfiguration {
}
