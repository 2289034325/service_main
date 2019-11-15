package com.acxca.ava.config;


import com.acxca.components.java.util.AliyunOSSClient;
import com.acxca.components.java.util.DateUtil;
import com.acxca.components.spring.config.JwtAuthConfig;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.aliyun.oss.OSSClient;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.acxca.onedollar.repository"})
@Import({JwtAuthConfig.class})
public class Config {

    @Autowired
    private Properties properties;

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource getDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        return loggingFilter;
    }

    @Bean
    RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000);
        requestFactory.setReadTimeout(1000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }

    @Bean
    public DateUtil dateUtil(){
        return new DateUtil();
    }

    @Bean
    public AliyunOSSClient aliyunOSSClient(){
        OSSClient ossClient = new OSSClient(properties.getOssEndpoint(),properties.getOssKey(),properties.getOssSecret());
        return new AliyunOSSClient(ossClient);
    }
}
