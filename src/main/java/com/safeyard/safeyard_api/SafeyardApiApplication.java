package com.safeyard.safeyard_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SafeyardApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SafeyardApiApplication.class, args);
    }
}
