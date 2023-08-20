package com.ccsc.ccsc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableOpenApi
@EnableScheduling
@EnableAsync
public class CcscApplication {

    public static void main(String[] args) {
        SpringApplication.run(CcscApplication.class, args);
    }

}
