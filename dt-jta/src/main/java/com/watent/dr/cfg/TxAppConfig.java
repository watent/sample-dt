package com.watent.dr.cfg;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/***
 * 系统配置
 */
@Configuration
public class TxAppConfig {

    /**
     * 订单系统数据库
     */
    @Bean(name = "orderDataSource")
    @Primary
    @ConfigurationProperties(prefix = "mall.order-datasource")
    public DataSource orderDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "orderJdbcTemplate")
    @Primary
    public JdbcTemplate orderJdbcTemplate(@Qualifier("orderDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * 调度系统数据库
     */
    @Bean(name = "dispatchDataSource")
    @ConfigurationProperties(prefix = "mall.dispatch-datasource")
    public DataSource dispatchDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dispatchJdbcTemplate")
    public JdbcTemplate dispatchJdbcTemplate(
            @Qualifier("dispatchDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
