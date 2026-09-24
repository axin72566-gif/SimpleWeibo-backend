package org.example.simpleweibobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用入口:Mapper 由 MyBatis-Plus 按主类包路径自动扫描 @Mapper 接口注册
 */
@SpringBootApplication
public class SimpleWeiboBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleWeiboBackendApplication.class, args);
    }

}
