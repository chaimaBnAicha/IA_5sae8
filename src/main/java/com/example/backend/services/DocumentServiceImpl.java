package com.example.backend.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service("documentService")
public class DocumentServiceImpl implements IDocumentService {

    private final Path documentStorageLocation;

    public DocumentServiceImpl() {
        this.documentStorageLocation = Paths.get("uploads/documents")
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.documentStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create directory for storing documents", ex);
        }
    }

    @Override
    public String saveDocument(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        if (filename.contains("..")) {
            throw new RuntimeException("Filename contains invalid path sequence " + filename);
        }

        Path targetLocation = this.documentStorageLocation.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    @Override
    public Resource loadDocument(String filename) throws IOException {
        Path filePath = getDocumentPath(filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("File not found " + filename);
        }
    }

    @Override
    public Path getDocumentPath(String filename) {
        // Use the same documentStorageLocation defined in the constructor
        return documentStorageLocation.resolve(filename).normalize();
    }
}
