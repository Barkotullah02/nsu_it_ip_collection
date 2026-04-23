package com.example.recordsapp.repository;

import com.example.recordsapp.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByNameContainingIgnoreCase(String name);

    List<Record> findByDepartmentContainingIgnoreCase(String department);

    List<Record> findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(String name, String department);

    @Query("SELECT DISTINCT r.department FROM Record r ORDER BY r.department")
    List<String> findAllDepartments();
}