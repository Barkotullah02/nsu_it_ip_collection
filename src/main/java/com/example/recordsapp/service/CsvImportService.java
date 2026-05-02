package com.example.recordsapp.service;

import com.example.recordsapp.model.Department;
import com.example.recordsapp.model.IpAddress;
import com.example.recordsapp.model.Record;
import com.example.recordsapp.repository.DepartmentRepository;
import com.example.recordsapp.repository.IpAddressRepository;
import com.example.recordsapp.repository.RecordRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CsvImportService {

    private final RecordRepository recordRepository;
    private final IpAddressRepository ipAddressRepository;
    private final DepartmentRepository departmentRepository;

    private final ConcurrentHashMap<String, ImportProgress> progressMap = new ConcurrentHashMap<>();

    public CsvImportService(RecordRepository recordRepository,
                          IpAddressRepository ipAddressRepository,
                          DepartmentRepository departmentRepository) {
        this.recordRepository = recordRepository;
        this.ipAddressRepository = ipAddressRepository;
        this.departmentRepository = departmentRepository;
    }

    public String startImport(List<CsvRecord> csvRecords) {
        String jobId = UUID.randomUUID().toString();
        ImportProgress progress = new ImportProgress();
        progress.setTotal(csvRecords.size());
        progress.setStatus("STARTED");
        progressMap.put(jobId, progress);

        new Thread(() -> {
            try {
                List<ImportResult> results = doImport(csvRecords, jobId);
                progress.setResults(results);
                progress.setStatus("COMPLETED");
            } catch (Exception e) {
                progress.setStatus("FAILED");
                progress.setError(e.getMessage());
            }
        }).start();

        return jobId;
    }

    public ImportProgress getProgress(String jobId) {
        return progressMap.get(jobId);
    }

    public List<ImportResult> doImport(List<CsvRecord> csvRecords, String jobId) {
        ImportProgress progress = progressMap.get(jobId);
        List<ImportResult> results = new ArrayList<>();
        int rowNumber = 1;
        int processed = 0;

        for (CsvRecord csv : csvRecords) {
            ImportResult result = new ImportResult();
            result.setRowNumber(rowNumber);
            result.setIpAddress(csv.getIpAddress());
            result.setName(csv.getName());
            result.setDepartment(csv.getDepartment());

            try {
                processRecord(csv);
                result.setSuccess(true);
                result.setMessage("Record created successfully");
            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage(e.getMessage());
            }

            results.add(result);
            processed++;
            progress.setProcessed(processed);
            rowNumber++;
        }

        return results;
    }

    public List<ImportResult> importRecords(List<CsvRecord> csvRecords) {
        String jobId = UUID.randomUUID().toString();
        return doImport(csvRecords, jobId);
    }

    @Transactional
    private void processRecord(CsvRecord csv) {
        String ipAddressStr = csv.getIpAddress();
        String departmentName = csv.getDepartment();
        String name = csv.getName();

        if (ipAddressStr == null || ipAddressStr.isBlank()) {
            throw new IllegalArgumentException("IP address is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        Department department = findOrCreateDepartment(departmentName);

        Optional<IpAddress> existingIp = ipAddressRepository.findByIpAddress(ipAddressStr);
        IpAddress ipAddress;

        if (existingIp.isPresent()) {
            ipAddress = existingIp.get();
        } else {
            ipAddress = new IpAddress();
            ipAddress.setIpAddress(ipAddressStr);
            ipAddress.setIsAssigned(true);
            ipAddress = ipAddressRepository.save(ipAddress);
        }

        if (ipAddress.getIsAssigned()) {
            throw new IllegalStateException("IP address " + ipAddressStr + " is not free");
        }

        ipAddress.setIsAssigned(true);
        ipAddress.setMacAddress(csv.getMacAddress());
        ipAddress.setRoom(csv.getRoom());
        ipAddressRepository.save(ipAddress);

        Record record = new Record();
        record.setName(name);
        record.setDesignation(csv.getDesignation());
        record.setExtNumber(csv.getExtNumber());
        record.setMacAddress(csv.getMacAddress());
        record.setRoom(csv.getRoom());
        record.setDepartment(department);
        record.setIpAddress(ipAddress);
        record.setAssignedAt(LocalDateTime.now());

        recordRepository.save(record);
    }

    private Department findOrCreateDepartment(String departmentName) {
        if (departmentName == null || departmentName.isBlank()) {
            return null;
        }
        return departmentRepository.findByNameIgnoreCase(departmentName.trim())
                .orElseGet(() -> {
                    Department newDept = new Department();
                    newDept.setName(departmentName.trim());
                    return departmentRepository.save(newDept);
                });
    }

    public static class CsvRecord {
        private String name;
        private String designation;
        private String extNumber;
        private String ipAddress;
        private String macAddress;
        private String room;
        private String department;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }
        public String getExtNumber() { return extNumber; }
        public void setExtNumber(String extNumber) { this.extNumber = extNumber; }
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        public String getMacAddress() { return macAddress; }
        public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
        public String getRoom() { return room; }
        public void setRoom(String room) { this.room = room; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
    }

    public static class ImportResult {
        private int rowNumber;
        private String ipAddress;
        private String name;
        private String department;
        private boolean success;
        private String message;

        public int getRowNumber() { return rowNumber; }
        public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ImportProgress {
        private int total;
        private int processed;
        private String status;
        private String error;
        private List<ImportResult> results;

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public int getProcessed() { return processed; }
        public void setProcessed(int processed) { this.processed = processed; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public List<ImportResult> getResults() { return results; }
        public void setResults(List<ImportResult> results) { this.results = results; }

        public int getPercentage() {
            return total > 0 ? (processed * 100) / total : 0;
        }
    }
}