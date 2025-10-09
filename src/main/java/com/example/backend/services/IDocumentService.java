package com.example.backend.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface IDocumentService {
    String saveDocument(MultipartFile file) throws IOException;
    Resource loadDocument(String filename) throws IOException;
    Path getDocumentPath(String filename);
}
