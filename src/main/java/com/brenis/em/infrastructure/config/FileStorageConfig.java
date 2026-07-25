package com.brenis.em.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {

    @Value("${app.uploads.path}")
    private String uploadPath;

    public String getComprobantesPath() {
        return uploadPath + "/comprobantes";
    }

    public String getPdfsPath() {
        return uploadPath + "/pdfs";
    }

    public String getUploadPath() {
        return uploadPath;
    }
}
