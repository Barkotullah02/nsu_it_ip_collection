package com.example.recordsapp.repository;

import com.example.recordsapp.model.Record;
import com.example.recordsapp.model.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByNameContainingIgnoreCase(String name);

    List<Record> findByDepartmentContainingIgnoreCase(String department);

    List<Record> findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(String name, String department);

    List<Record> findByStatus(RecordStatus status);

    @Query("SELECT DISTINCT r.department FROM Record r WHERE r.department IS NOT NULL ORDER BY r.department")
    List<String> findAllDepartments();

    @Query("SELECT DISTINCT r.department FROM Record r WHERE r.status = :status AND r.department IS NOT NULL ORDER BY r.department")
    List<String> findAllDepartmentsByStatus(RecordStatus status);
}