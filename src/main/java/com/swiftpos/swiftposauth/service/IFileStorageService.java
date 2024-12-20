package com.swiftpos.swiftposauth.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {
    String storeFile(MultipartFile file, String subfolder);

    Resource getFile(String fileName, String subfolder);

    void deleteFile(String filePath);
}
