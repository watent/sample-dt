package com.watent.dr.mq;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DispatchApplication {
    public static void main(String[] args) {

        new SpringApplicationBuilder(DispatchApplication.class).web(WebApplicationType.SERVLET).run(args);
        System.out.println("#分配外卖小哥的系统启动完毕");
    }
}
