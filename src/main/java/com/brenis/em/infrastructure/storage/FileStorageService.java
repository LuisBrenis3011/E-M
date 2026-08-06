package com.brenis.em.infrastructure.storage;

import com.brenis.em.infrastructure.config.FileStorageConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path comprobantesPath;
    private final Path pdfsPath;

    public FileStorageService(FileStorageConfig config) {
        this.comprobantesPath = Paths.get(config.getComprobantesPath());
        this.pdfsPath = Paths.get(config.getPdfsPath());
        createDirectories();
    }

    private void createDirectories() {
        try {
            Files.createDirectories(comprobantesPath);
            Files.createDirectories(pdfsPath);
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron crear los directorios de almacenamiento", e);
        }
    }

    public String storeComprobante(MultipartFile file) {
        return storeFile(file, comprobantesPath, "comprobantes");
    }

    public String storePdf(byte[] pdfBytes, String fileName) {
        try {
            String storedName = UUID.randomUUID() + "_" + fileName;
            Path targetPath = pdfsPath.resolve(storedName);
            Files.write(targetPath, pdfBytes, StandardOpenOption.CREATE);
            return "/uploads/pdfs/" + storedName;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar PDF", e);
        }
    }

    private String storeFile(MultipartFile file, Path basePath, String subfolder) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String storedFilename = UUID.randomUUID() + "_" + originalFilename;

        try {
            Path targetPath = basePath.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + subfolder + "/" + storedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo: " + originalFilename, e);
        }
    }

    public byte[] readFile(String relativePath) {
        try {
            Path fullPath = Paths.get(".").resolve(relativePath.replaceFirst("^/", ""));
            return Files.readAllBytes(fullPath.toAbsolutePath().normalize());
        } catch (IOException e) {
            throw new RuntimeException("Error al leer archivo: " + relativePath, e);
        }
    }

    public void deleteFile(String relativePath) {
        try {
            Path fullPath = Paths.get(".").resolve(relativePath.replaceFirst("^/", ""));
            Files.deleteIfExists(fullPath.toAbsolutePath().normalize());
        } catch (IOException e) {
            // best effort — el archivo puede no existir
        }
    }
}
