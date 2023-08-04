package com.simple.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "gzy")
@Data
public class AppProperties {
    private String addr;
    private String name;
    private String age;


    @PostConstruct
    public void sout(){
        System.out.println(this.addr);
        System.out.println(this.name);
        System.out.println(this.age);
    }
}
