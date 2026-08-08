package org.example.simpleweibobackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.simpleweibobackend")
public class SimpleWeiboBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleWeiboBackendApplication.class, args);
    }

}
