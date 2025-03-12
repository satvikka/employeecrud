package com.satvika.employeecrud.repository;

import com.satvika.employeecrud.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
