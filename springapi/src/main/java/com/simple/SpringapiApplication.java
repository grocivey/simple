package com.simple;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan(basePackages = {"com.simple.api.mapper"})
@EnableSwagger2
public class SpringapiApplication {

    public static void main(String[] args) {

        SpringApplication.run(SpringapiApplication.class, args);
    }

}
