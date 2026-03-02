package com.readyroad.readyroadbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReadyroadApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadyroadApplication.class, args);
    }

}
