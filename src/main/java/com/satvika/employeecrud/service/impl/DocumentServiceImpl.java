package com.satvika.employeecrud.service.impl;

import com.satvika.employeecrud.entity.Document;
import com.satvika.employeecrud.repository.DocumentRepository;
import com.satvika.employeecrud.service.DocumentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private static final String UPLOAD_DIR = "uploads/";

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public String saveFile(MultipartFile file) {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, file.getBytes());

            String publicUrl = "http://localhost:8080/uploads/" + fileName;

            Document document = new Document();
            document.setFileName(fileName);
            document.setFileUrl(publicUrl);
            documentRepository.save(document);

            return publicUrl;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}

/*
package com.satvika.employeecrud.service.impl;

import com.satvika.employeecrud.entity.Document;
import com.satvika.employeecrud.repository.DocumentRepository;
import com.satvika.employeecrud.service.DocumentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private static final String UPLOAD_DIR = "uploads/";

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public String saveFile(MultipartFile file) {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, file.getBytes());

            Document document = new Document();
            document.setFileName(fileName);
            document.setFileUrl(filePath.toString());
            documentRepository.save(document);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
*/
