package org.example.simpleweibobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SimpleWeiboBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleWeiboBackendApplication.class, args);
    }

}
