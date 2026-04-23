package com.example.recordsapp.service;

import com.example.recordsapp.model.IpAddress;
import com.example.recordsapp.model.IpHistory;
import com.example.recordsapp.model.Record;
import com.example.recordsapp.repository.IpAddressRepository;
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
    private final IpAddressRepository ipAddressRepository;
    private final IpHistoryRepository ipHistoryRepository;

    public RecordService(RecordRepository recordRepository, IpAddressRepository ipAddressRepository, IpHistoryRepository ipHistoryRepository) {
        this.recordRepository = recordRepository;
        this.ipAddressRepository = ipAddressRepository;
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

    public List<String> getAllDepartments() {
        return recordRepository.findAllDepartments();
    }

    public Optional<Record> findById(Long id) {
        return recordRepository.findById(id);
    }

    public Optional<IpAddress> findIpAddressById(Long id) {
        return ipAddressRepository.findById(id);
    }

    public List<IpAddress> findAllIpAddresses() {
        return ipAddressRepository.findAll();
    }

    public List<IpAddress> findFreeIpAddresses() {
        return ipAddressRepository.findByIsAssigned(false);
    }

    public List<IpAddress> findAssignedIpAddresses() {
        return ipAddressRepository.findByIsAssigned(true);
    }

    @Transactional
    public Record save(Record record, String ipAddressStr) {
        IpAddress ipAddress = ipAddressRepository.findByIpAddress(ipAddressStr)
                .orElseGet(() -> {
                    IpAddress newIp = new IpAddress();
                    newIp.setIpAddress(ipAddressStr);
                    newIp.setIsAssigned(true);
                    return ipAddressRepository.save(newIp);
                });

        if (ipAddress.getIsAssigned()) {
            throw new IllegalStateException("IP address is already assigned");
        }

        ipAddress.setIsAssigned(true);
        ipAddressRepository.save(ipAddress);

        record.setIpAddress(ipAddress);
        record.setAssignedAt(LocalDateTime.now());

        if (record.getId() != null) {
            record.setUpdatedAt(LocalDateTime.now());
        }

        return recordRepository.save(record);
    }

    @Transactional
    public IpAddress saveIpAddress(IpAddress ipAddress) {
        if (ipAddress.getId() != null) {
            ipAddress.setUpdatedAt(LocalDateTime.now());
        }
        return ipAddressRepository.save(ipAddress);
    }

    @Transactional
    public void deleteById(Long id) {
        recordRepository.deleteById(id);
    }

    @Transactional
    public void deleteIpAddressById(Long id) {
        ipAddressRepository.deleteById(id);
    }

    @Transactional
    public Record markAsFree(Long id) {
        Optional<Record> recordOpt = recordRepository.findById(id);
        if (recordOpt.isPresent()) {
            Record record = recordOpt.get();
            IpAddress ipAddress = record.getIpAddressEntity();

            if (ipAddress == null) {
                return null;
            }

            IpHistory history = new IpHistory();
            history.setIpAddress(ipAddress.getIpAddress());
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
            record.setReleasedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now());
            record.setSl(null);

            ipAddress.setIsAssigned(false);
            ipAddressRepository.save(ipAddress);

            return recordRepository.save(record);
        }
        return null;
    }

    @Transactional
    public Record assignToNewUser(Long id, Record newRecord) {
        Optional<Record> recordOpt = recordRepository.findById(id);
        if (recordOpt.isPresent()) {
            Record existing = recordOpt.get();
            IpAddress ipAddress = existing.getIpAddressEntity();

            if (ipAddress == null || !ipAddress.getIsAssigned()) {
                existing.setName(newRecord.getName());
                existing.setDesignation(newRecord.getDesignation());
                existing.setExtNumber(newRecord.getExtNumber());
                existing.setMacAddress(newRecord.getMacAddress());
                existing.setRoom(newRecord.getRoom());
                existing.setDepartment(newRecord.getDepartment());
                existing.setAssignedAt(LocalDateTime.now());
                existing.setReleasedAt(null);
                existing.setUpdatedAt(LocalDateTime.now());
                existing.setSl(newRecord.getSl());

                ipAddress.setIsAssigned(true);
                ipAddressRepository.save(ipAddress);

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