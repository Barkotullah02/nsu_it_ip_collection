package com.example.recordsapp.repository;

import com.example.recordsapp.model.Department;
import com.example.recordsapp.model.IpAddress;
import com.example.recordsapp.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByNameContainingIgnoreCase(String name);

    List<Record> findByDepartment(Department department);

    List<Record> findByIpAddress(IpAddress ipAddress);

    @Query("SELECT DISTINCT d.name FROM Department d ORDER BY d.name")
    List<String> findAllDepartments();

    @Query("SELECT DISTINCT d.name FROM Department d JOIN Record r ON r.department = d WHERE r.ipAddress.isAssigned = true ORDER BY d.name")
    List<String> findAllDepartmentsByStatusActive();
}