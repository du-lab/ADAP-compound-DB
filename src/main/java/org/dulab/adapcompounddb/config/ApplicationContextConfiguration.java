package org.dulab.adapcompounddb.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = false)
@EnableJpaRepositories(basePackages = "org.dulab.adapcompounddb.site.repositories", entityManagerFactoryRef = "entityManagerFactoryBean", transactionManagerRef = "jpaTransactionManager")
@ComponentScan(basePackages = { "org.dulab.adapcompounddb.site",
        "org.dulab.adapcompounddb.rest" }, excludeFilters = @ComponentScan.Filter({ Controller.class,
                ControllerAdvice.class }))
@Import({ WebSecurityConfiguration.class })
public class ApplicationContextConfiguration {

    @Autowired
    DataSource dataSource;

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        return validator;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        final MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(localValidatorFactoryBean());
        return processor;
    }

    @Bean
//    @Profile("!test")
    public DataSource adapCompoundDbDataSource() {
        final JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        return lookup.getDataSource("jdbc/AdapCompoundDbDataSource");
    }

//    @Bean
//    @Profile("test")
//    public DataSource adapCompoundDbDataSourceTest() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setUrl("jdbc:mysql://localhost:3306/adapcompounddb");
//        dataSource.setUsername("root");
//        dataSource.setPassword("sesame");
//        return dataSource;
//    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        final HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5InnoDBDialect");

        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(adapter);
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("org.dulab.adapcompounddb.models.entities");
        factory.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
        factory.setValidationMode(ValidationMode.NONE);

        final Map<String, Object> jpaPropertyMap = new HashMap<>();
        jpaPropertyMap.put("javax.persistence.schema-generation.database.action", "none");
//        jpaPropertyMap.put("hibernate.format_sql", true);
//        jpaPropertyMap.put("hibernate.use_sql_comments", true);
        jpaPropertyMap.put("hibernate.show_sql", true);
//        jpaPropertyMap.put("hibernate.generate_statistics", true);
        factory.setJpaPropertyMap(jpaPropertyMap);

        return factory;
    }

    @Bean
    public PlatformTransactionManager jpaTransactionManager() {
        return new JpaTransactionManager(entityManagerFactoryBean().getObject());
    }
}
