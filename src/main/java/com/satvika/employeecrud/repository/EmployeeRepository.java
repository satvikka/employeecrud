package com.satvika.employeecrud.repository;

import com.satvika.employeecrud.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    public Optional<Employee> findById(Long id);

    List<Employee> findByDeletedFalse();

    // Optionally, if you want to use soft delete in queries, you can create a custom query to find only non-deleted employees
    @Query("SELECT e FROM Employee e WHERE e.deleted = false")
    List<Employee> findActiveEmployees();
    Page<Employee> findByDeletedFalse(Pageable pageable);


}
