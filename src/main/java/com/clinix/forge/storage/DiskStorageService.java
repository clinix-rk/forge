package com.clinix.forge.storage;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class DiskStorageService {

    private final Path rootLocation = Paths.get("uploads");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    public String store(MultipartFile file, Long patientId) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Failed to store empty file.");
            }
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("File size exceeds limit of 10 MB.");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                throw new IllegalArgumentException("Only PDF files are allowed.");
            }

            Path patientDir = rootLocation.resolve("patients").resolve(patientId.toString());
            Files.createDirectories(patientDir);

            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            if (filename.contains("..")) {
                throw new IllegalArgumentException("Cannot store file with relative path outside directory " + filename);
            }

            Path destinationFile = patientDir.resolve(filename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return destinationFile.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public void delete(String location) {
        try {
            Path file = Paths.get(location);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.error("Failed to delete file at location: {}", location, e);
        }
    }

    public byte[] load(String location) {
        try {
            return Files.readAllBytes(Paths.get(location));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file at location: " + location, e);
        }
    }
}
