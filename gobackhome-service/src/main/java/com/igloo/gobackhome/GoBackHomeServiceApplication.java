package com.igloo.gobackhome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.igloo.gobackhome", "com.igloo.common"})
public class GoBackHomeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoBackHomeServiceApplication.class, args);
    }
}
