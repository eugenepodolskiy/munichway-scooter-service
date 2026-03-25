package com.example.munichway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MunichwayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MunichwayApplication.class, args);
    }

}
