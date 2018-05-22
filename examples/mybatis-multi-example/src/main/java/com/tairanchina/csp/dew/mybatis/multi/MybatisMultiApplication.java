package com.tairanchina.csp.dew.mybatis.multi;


import com.tairanchina.csp.dew.jdbc.mybatis.annotion.DewMapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * desription:
 * Created by ding on 2017/12/28.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy(exposeProxy = true,proxyTargetClass = true)
@DewMapperScan(basePackages = "com.tairanchina.csp.dew.mybatis.multi.mapper")
public class MybatisMultiApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MybatisMultiApplication.class).web(false).run(args);
    }


}
