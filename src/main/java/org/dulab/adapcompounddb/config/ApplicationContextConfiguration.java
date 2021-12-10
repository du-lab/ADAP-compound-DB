package org.dulab.adapcompounddb.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Configuration
@EnableAsync
@EnableTransactionManagement(mode = AdviceMode.PROXY, proxyTargetClass = false)
@EnableJpaRepositories(basePackages = "org.dulab.adapcompounddb.site.repositories",
        entityManagerFactoryRef = "entityManagerFactoryBean", transactionManagerRef = "jpaTransactionManager")
@ComponentScan(basePackages = {"org.dulab.adapcompounddb.site", "org.dulab.adapcompounddb.rest"},
        excludeFilters = @ComponentScan.Filter({Controller.class, ControllerAdvice.class}))
@Import({WebSecurityConfiguration.class})
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
    public DataSource adapCompoundDbDataSource() {
        final JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        return lookup.getDataSource("jdbc/AdapCompoundDbDataSource");
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        final HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");

        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(adapter);
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("org.dulab.adapcompounddb.models.entities");
        factory.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
        factory.setValidationMode(ValidationMode.NONE);

        final Map<String, Object> jpaPropertyMap = new HashMap<>();
        jpaPropertyMap.put("javax.persistence.schema-generation.database.action", "none");
        jpaPropertyMap.put("hibernate.dialect.storage_engine", "innodb");
        jpaPropertyMap.put("hibernate.order_by.default_null_ordering", "last");
        jpaPropertyMap.put("hibernate.enable_lazy_load_no_trans", true);
                jpaPropertyMap.put("hibernate.format_sql", true);
//                jpaPropertyMap.put("hibernate.use_sql_comments", true);
                jpaPropertyMap.put("hibernate.show_sql", true);
//                jpaPropertyMap.put("hibernate.generate_statistics", true);
//                jpaPropertyMap.put("hibernate.SQL", "DEBUG");
//                jpaPropertyMap.put("hibernate.type.descriptor.sql.BasicBinder", "TRACE");
//                jpaPropertyMap.put("org.hibernate.cache", "DEBUG");
        factory.setJpaPropertyMap(jpaPropertyMap);

        return factory;
    }

    @Bean
    public PlatformTransactionManager jpaTransactionManager() {
        return new JpaTransactionManager(entityManagerFactoryBean().getObject());
    }

    @Bean("email_properties")
    public Properties getEmailProperties() {
        final Properties prop = new Properties();
        try {
            final Context initContext = new InitialContext();
            final Context envContext = (Context) initContext.lookup("java:/comp/env");

            // Used for smtp properties
            prop.put("mail.smtp.auth", true);
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.host", envContext.lookup("email_smtp_host"));
            prop.put("mail.smtp.port", envContext.lookup("email_smtp_port"));
            prop.put("mail.smtp.ssl.trust", envContext.lookup("email_smtp_host"));

            // Used for smtp authentication
            prop.put("username", envContext.lookup("email_username"));
            prop.put("password", envContext.lookup("email_password"));

            // Used as a FROM/TO email addresses
            prop.put("email_from", envContext.lookup("email_from"));
            prop.put("email_to", envContext.lookup("email_to"));
        } catch (final NamingException e) {
        }

        return prop;
    }

    @Bean
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);  // Set the number of threads to 1 because EntityManager throws errors when run in parallel threads
        return executor;
    }
}
