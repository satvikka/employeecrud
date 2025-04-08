package com.satvika.employeecrud.repository;

import com.satvika.employeecrud.entity.Document;
import com.satvika.employeecrud.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    void deleteByEmployee(Employee employee);

    Document findByEmployeeId(Long id);
}


