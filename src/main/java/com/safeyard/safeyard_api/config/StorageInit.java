package com.safeyard.safeyard_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class StorageInit implements CommandLineRunner {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void run(String... args) throws Exception {
        Path root = Paths.get(uploadDir, "motos");
        Files.createDirectories(root);
    }
}
