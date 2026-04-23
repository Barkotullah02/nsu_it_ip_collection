package com.example.recordsapp.repository;

import com.example.recordsapp.model.IpAddress;
import com.example.recordsapp.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByNameContainingIgnoreCase(String name);

    List<Record> findByDepartmentContainingIgnoreCase(String department);

    List<Record> findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(String name, String department);

    List<Record> findByIpAddress(IpAddress ipAddress);

    @Query("SELECT DISTINCT r.department FROM Record r WHERE r.department IS NOT NULL ORDER BY r.department")
    List<String> findAllDepartments();

    @Query("SELECT DISTINCT r.department FROM Record r WHERE r.ipAddress.isAssigned = true AND r.department IS NOT NULL ORDER BY r.department")
    List<String> findAllDepartmentsByStatusActive();
}