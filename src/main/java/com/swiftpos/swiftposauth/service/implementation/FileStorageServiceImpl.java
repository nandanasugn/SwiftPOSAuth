package com.swiftpos.swiftposauth.service.implementation;

import com.swiftpos.swiftposauth.service.IFileStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class FileStorageServiceImpl implements IFileStorageService {
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @Override
    public String storeFile(MultipartFile file, String subfolder) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

            if (!Arrays.asList(".jpg", ".jpeg", ".png").contains(extension.toLowerCase())) {
                throw new RuntimeException("Unsupported file extension: " + extension);
            }

            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;

            Path targetLocation = fileStorageLocation.resolve(subfolder).resolve(fileName);

            Files.createDirectories(targetLocation.getParent());
            Files.copy(file.getInputStream(), targetLocation);

            return targetLocation.toString();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't store file", e);
        }
    }

    @Override
    public Resource getFile(String fileName, String subfolder) {
        try {
            Path filePath = fileStorageLocation.resolve(subfolder).resolve(fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Couldn't read file: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Couldn't read file: " + fileName, e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't delete file", e);
        }
    }
}
