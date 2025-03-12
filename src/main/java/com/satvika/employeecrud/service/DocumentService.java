package com.satvika.employeecrud.service;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    String saveFile(MultipartFile file);
}
