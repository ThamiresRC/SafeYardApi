package com.safeyard.safeyard_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class UploadDirInitializer implements CommandLineRunner {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void run(String... args) throws Exception {
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
    }
}
