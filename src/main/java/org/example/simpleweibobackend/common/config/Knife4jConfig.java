package org.example.simpleweibobackend.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SimpleWeibo API 文档")
                        .description("简易微博后端接口文档")
                        .version("v1")
                        .contact(new Contact().name("SimpleWeibo"))
                        .license(new License().name("Apache 2.0")));
    }
}
