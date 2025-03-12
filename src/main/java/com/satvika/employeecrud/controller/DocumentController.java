package com.satvika.employeecrud.controller;

import com.satvika.employeecrud.service.DocumentService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = documentService.saveFile(file);
        return ResponseEntity.ok("File uploaded successfully: " + fileUrl);
    }

    @GetMapping("/uploads/{filename}")
    public ResponseEntity<FileSystemResource> getFile(@PathVariable String filename) {
        File file = new File("C:/Users/satvika/IdeaProjects/employeecrud/uploads" + filename);
        if (file.exists()) {
            FileSystemResource resource = new FileSystemResource(file);
            return ResponseEntity.ok(resource);
        }
        return ResponseEntity.notFound().build();
    }
}
