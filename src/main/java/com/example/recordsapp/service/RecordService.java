package com.example.recordsapp.service;

import com.example.recordsapp.model.IpHistory;
import com.example.recordsapp.model.Record;
import com.example.recordsapp.model.RecordStatus;
import com.example.recordsapp.repository.IpHistoryRepository;
import com.example.recordsapp.repository.RecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecordService {

    private final RecordRepository recordRepository;
    private final IpHistoryRepository ipHistoryRepository;

    public RecordService(RecordRepository recordRepository, IpHistoryRepository ipHistoryRepository) {
        this.recordRepository = recordRepository;
        this.ipHistoryRepository = ipHistoryRepository;
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

    public List<Record> findByStatus(RecordStatus status) {
        return recordRepository.findByStatus(status);
    }

    public List<String> getAllDepartments() {
        return recordRepository.findAllDepartments();
    }

    public List<String> getAllDepartmentsByStatus(RecordStatus status) {
        return recordRepository.findAllDepartmentsByStatus(status);
    }

    public Optional<Record> findById(Long id) {
        return recordRepository.findById(id);
    }

    @Transactional
    public Record save(Record record) {
        if (record.getId() != null) {
            record.setUpdatedAt(LocalDateTime.now());
        }
        if (record.getStatus() == null) {
            record.setStatus(RecordStatus.ACTIVE);
        }
        if (record.getAssignedAt() == null && record.getStatus() == RecordStatus.ACTIVE) {
            record.setAssignedAt(LocalDateTime.now());
        }
        return recordRepository.save(record);
    }

    @Transactional
    public void deleteById(Long id) {
        recordRepository.deleteById(id);
    }

    @Transactional
    public Record markAsFree(Long id) {
        Optional<Record> recordOpt = recordRepository.findById(id);
        if (recordOpt.isPresent()) {
            Record record = recordOpt.get();

            IpHistory history = new IpHistory();
            history.setIpAddress(record.getIpAddress());
            history.setName(record.getName());
            history.setDesignation(record.getDesignation());
            history.setExtNumber(record.getExtNumber());
            history.setMacAddress(record.getMacAddress());
            history.setRoom(record.getRoom());
            history.setDepartment(record.getDepartment());
            history.setAssignedAt(record.getAssignedAt());
            history.setReleasedAt(LocalDateTime.now());
            history.setActive(false);
            ipHistoryRepository.save(history);

            record.setName(null);
            record.setDesignation(null);
            record.setExtNumber(null);
            record.setMacAddress(null);
            record.setRoom(null);
            record.setDepartment(null);
            record.setStatus(RecordStatus.FREE);
            record.setReleasedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now());

            return recordRepository.save(record);
        }
        return null;
    }

    @Transactional
    public Record assignToNewUser(Long id, Record newRecord) {
        Optional<Record> recordOpt = recordRepository.findById(id);
        if (recordOpt.isPresent()) {
            Record existing = recordOpt.get();

            if (existing.getStatus() == RecordStatus.FREE) {
                existing.setName(newRecord.getName());
                existing.setDesignation(newRecord.getDesignation());
                existing.setExtNumber(newRecord.getExtNumber());
                existing.setMacAddress(newRecord.getMacAddress());
                existing.setRoom(newRecord.getRoom());
                existing.setDepartment(newRecord.getDepartment());
                existing.setStatus(RecordStatus.ACTIVE);
                existing.setAssignedAt(LocalDateTime.now());
                existing.setReleasedAt(null);
                existing.setUpdatedAt(LocalDateTime.now());
                existing.setSl(newRecord.getSl());

                return recordRepository.save(existing);
            }
        }
        return null;
    }

    public List<IpHistory> findHistoryByIpAddress(String ipAddress) {
        return ipHistoryRepository.findByIpAddressOrderByAssignedAtDesc(ipAddress);
    }

    public List<IpHistory> findAllHistory() {
        return ipHistoryRepository.findByOrderByAssignedAtDesc();
    }

    @Transactional
    public IpHistory saveHistory(IpHistory history) {
        return ipHistoryRepository.save(history);
    }
}