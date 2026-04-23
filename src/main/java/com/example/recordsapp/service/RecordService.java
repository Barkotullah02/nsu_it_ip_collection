package com.example.recordsapp.service;

import com.example.recordsapp.model.Record;
import com.example.recordsapp.repository.RecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecordService {

    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public List<Record> findAll() {
        return recordRepository.findAll();
    }

    public List<Record> findByNameContaining(String name) {
        return recordRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Record> findByDepartmentContaining(String department) {
        return recordRepository.findByDepartmentContainingIgnoreCase(department);
    }

    public List<Record> findByNameContainingOrDepartmentContaining(String name, String department) {
        return recordRepository.findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(name, department);
    }

    public List<String> getAllDepartments() {
        return recordRepository.findAllDepartments();
    }

    public Optional<Record> findById(Long id) {
        return recordRepository.findById(id);
    }

    @Transactional
    public Record save(Record record) {
        if (record.getId() != null) {
            record.setUpdatedAt(LocalDateTime.now());
        }
        return recordRepository.save(record);
    }

    @Transactional
    public void deleteById(Long id) {
        recordRepository.deleteById(id);
    }
}