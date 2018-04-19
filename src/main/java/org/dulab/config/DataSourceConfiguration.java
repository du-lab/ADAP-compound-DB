package org.dulab.config;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

public interface DataSourceConfiguration {

    DataSource adapCompoundDbDataSource();

    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean();

    PlatformTransactionManager jpaTransactionManager();
}
