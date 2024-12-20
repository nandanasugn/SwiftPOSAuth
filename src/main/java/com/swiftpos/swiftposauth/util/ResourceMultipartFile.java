package com.swiftpos.swiftposauth.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Data
@RequiredArgsConstructor
public class ResourceMultipartFile implements MultipartFile {
    private final Resource resource;
    private final String originalFilename;

    @Override
    public String getName() {
        return resource.getFilename();
    }

    @Override
    public String getContentType() {
        return originalFilename;
    }

    @Override
    public boolean isEmpty() {
        try {
            return resource.contentLength() <= 0;
        } catch (IOException e) {
            return true;
        }
    }

    @Override
    public long getSize() {
        try {
            return resource.contentLength();
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public byte[] getBytes() throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return resource.getInputStream();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (InputStream inputStream = getInputStream(); OutputStream outputStream = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
