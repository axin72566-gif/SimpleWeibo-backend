package org.example.simpleweibobackend;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = "org.example.simpleweibobackend", annotationClass = Mapper.class)
@EnableScheduling
public class SimpleWeiboBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleWeiboBackendApplication.class, args);
    }

}
