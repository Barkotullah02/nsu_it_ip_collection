package com.example.recordsapp.config;

import com.example.recordsapp.model.AppUser;
import com.example.recordsapp.model.Department;
import com.example.recordsapp.model.IpAddress;
import com.example.recordsapp.model.Record;
import com.example.recordsapp.model.Role;
import com.example.recordsapp.repository.DepartmentRepository;
import com.example.recordsapp.repository.IpAddressRepository;
import com.example.recordsapp.repository.RecordRepository;
import com.example.recordsapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository, IpAddressRepository ipAddressRepository, 
                                       DepartmentRepository departmentRepository, RecordRepository recordRepository, 
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                AppUser superAdmin = new AppUser();
                superAdmin.setUsername("superadmin");
                superAdmin.setPassword(passwordEncoder.encode("super123"));
                superAdmin.setFullName("System Administrator");
                superAdmin.setDepartment("IT");
                superAdmin.setIsActive(true);
                superAdmin.setRoles(Set.of(Role.SUPER_ADMIN));

                AppUser editor = new AppUser();
                editor.setUsername("editor");
                editor.setPassword(passwordEncoder.encode("editor123"));
                editor.setFullName("Data Editor");
                editor.setDepartment("Administration");
                editor.setIsActive(true);
                editor.setRoles(Set.of(Role.EDITOR));

                AppUser viewer = new AppUser();
                viewer.setUsername("viewer");
                viewer.setPassword(passwordEncoder.encode("viewer123"));
                viewer.setFullName("Data Viewer");
                viewer.setDepartment("General");
                viewer.setIsActive(true);
                viewer.setRoles(Set.of(Role.VIEWER));

                userRepository.save(superAdmin);
                userRepository.save(editor);
                userRepository.save(viewer);
            }

            if (departmentRepository.count() == 0) {
                List<Department> departments = Arrays.asList(
                    createDepartment("Department of ECE", "Electrical and Computer Engineering"),
                    createDepartment("Department of CSE", "Computer Science and Engineering"),
                    createDepartment("Department of EEE", "Electrical and Electronic Engineering"),
                    createDepartment("Department of CE", "Civil Engineering"),
                    createDepartment("Department of ICE", "Information and Communication Engineering"),
                    createDepartment("Department of IT", "Information Technology"),
                    createDepartment("Administration", "Administrative Office"),
                    createDepartment("Library", "Central Library"),
                    createDepartment("IT Department", "Information Technology Services")
                );
                for (Department dept : departments) {
                    departmentRepository.save(dept);
                }
            }

            if (ipAddressRepository.count() == 0) {
                List<IpAddress> sampleIps = Arrays.asList(
                    createIpAddress("192.168.1.101", "00:1A:2B:3C:4D:01", "Room 101", true),
                    createIpAddress("192.168.1.102", "00:1A:2B:3C:4D:02", "Room 102", true),
                    createIpAddress("192.168.1.103", "00:1A:2B:3C:4D:03", "Room 103", true),
                    createIpAddress("192.168.1.104", "00:1A:2B:3C:4D:04", "Room 104", true),
                    createIpAddress("192.168.1.105", "00:1A:2B:3C:4D:05", "Room 105", true),
                    createIpAddress("192.168.1.106", "00:1A:2B:3C:4D:06", "Room 201", true),
                    createIpAddress("192.168.1.107", "00:1A:2B:3C:4D:07", "Room 202", true),
                    createIpAddress("192.168.1.108", "00:1A:2B:3C:4D:08", "Room 203", true),
                    createIpAddress("192.168.1.109", "00:1A:2B:3C:4D:09", "Lab 101", true),
                    createIpAddress("192.168.1.110", "00:1A:2B:3C:4D:10", "Lab 102", true),
                    createIpAddress("192.168.1.111", "00:1A:2B:3C:4D:11", "Room 301", true),
                    createIpAddress("192.168.1.112", "00:1A:2B:3C:4D:12", "Room 302", true),
                    createIpAddress("192.168.1.113", "00:1A:2B:3C:4D:13", "Room 401", true),
                    createIpAddress("192.168.1.114", "00:1A:2B:3C:4D:14", "Room 402", true),
                    createIpAddress("192.168.1.115", "00:1A:2B:3C:4D:15", "Office 101", true),
                    createIpAddress("192.168.1.120", null, "Storage Room", false),
                    createIpAddress("192.168.1.121", null, "Storage Room", false),
                    createIpAddress("192.168.1.122", null, "Storage Room", false)
                );

                for (IpAddress ip : sampleIps) {
                    ipAddressRepository.save(ip);
                }

                List<IpAddress> savedIps = ipAddressRepository.findAll();
                Department eceDept = departmentRepository.findByNameIgnoreCase("Department of ECE").orElse(null);
                Department cseDept = departmentRepository.findByNameIgnoreCase("Department of CSE").orElse(null);
                Department eeeDept = departmentRepository.findByNameIgnoreCase("Department of EEE").orElse(null);
                Department ceDept = departmentRepository.findByNameIgnoreCase("Department of CE").orElse(null);
                Department iceDept = departmentRepository.findByNameIgnoreCase("Department of ICE").orElse(null);
                Department itDept = departmentRepository.findByNameIgnoreCase("Department of IT").orElse(null);
                Department adminDept = departmentRepository.findByNameIgnoreCase("Administration").orElse(null);

                if (recordRepository.count() == 0) {
                    List<Record> sampleRecords = Arrays.asList(
                        createRecord(1, savedIps.get(0), eceDept, "Dr. Ahmed Hassan", "Professor", "234", "Room 101", "00:1A:2B:3C:4D:01"),
                        createRecord(2, savedIps.get(1), cseDept, "Dr. Fatema Karim", "Associate Professor", "235", "Room 102", "00:1A:2B:3C:4D:02"),
                        createRecord(3, savedIps.get(2), eeeDept, "Dr. Mohammad Ali", "Professor", "236", "Room 103", "00:1A:2B:3C:4D:03"),
                        createRecord(4, savedIps.get(3), iceDept, "Dr. Sarah Ahmed", "Assistant Professor", "237", "Room 104", "00:1A:2B:3C:4D:04"),
                        createRecord(5, savedIps.get(4), ceDept, "Dr. Rahman Islam", "Professor", "238", "Room 105", "00:1A:2B:3C:4D:05"),
                        createRecord(6, savedIps.get(5), eceDept, "Dr. Nilufar Yasmin", "Professor", "239", "Room 201", "00:1A:2B:3C:4D:06"),
                        createRecord(7, savedIps.get(6), cseDept, "Mr. Khan", "Lecturer", "240", "Room 202", "00:1A:2B:3C:4D:07"),
                        createRecord(8, savedIps.get(7), eeeDept, "Mrs. Akter", "Lecturer", "241", "Room 203", "00:1A:2B:3C:4D:08"),
                        createRecord(9, savedIps.get(8), eceDept, "Mr. Hossain", "Lab Officer", "242", "Lab 101", "00:1A:2B:3C:4D:09"),
                        createRecord(10, savedIps.get(9), cseDept, "Ms. Aktar", "Lab Officer", "243", "Lab 102", "00:1A:2B:3C:4D:10"),
                        createRecord(11, savedIps.get(10), eeeDept, "Mr. Rahman", "Researcher", "244", "Room 301", "00:1A:2B:3C:4D:11"),
                        createRecord(12, savedIps.get(11), ceDept, "Mrs. Akhter", "Researcher", "245", "Room 302", "00:1A:2B:3C:4D:12"),
                        createRecord(13, savedIps.get(12), iceDept, "Dr. Islam", "Professor", "246", "Room 401", "00:1A:2B:3C:4D:13"),
                        createRecord(14, savedIps.get(13), itDept, "Mr. Das", "Technical Officer", "247", "Room 402", "00:1A:2B:3C:4D:14"),
                        createRecord(15, savedIps.get(14), adminDept, "Mrs. Haque", "Administrative", "248", "Office 101", "00:1A:2B:3C:4D:15")
                    );

                    for (Record record : sampleRecords) {
                        recordRepository.save(record);
                    }
                }
            }
        };
    }

    private Department createDepartment(String name, String description) {
        Department dept = new Department();
        dept.setName(name);
        dept.setDescription(description);
        return dept;
    }

    private IpAddress createIpAddress(String ip, String mac, String room, boolean assigned) {
        IpAddress ipAddress = new IpAddress();
        ipAddress.setIpAddress(ip);
        ipAddress.setMacAddress(mac);
        ipAddress.setRoom(room);
        ipAddress.setIsAssigned(assigned);
        return ipAddress;
    }

    private Record createRecord(Integer sl, IpAddress ipAddress, Department department, String name, String designation, String ext, String room, String mac) {
        Record record = new Record();
        record.setSl(sl);
        record.setIpAddress(ipAddress);
        record.setDepartment(department);
        record.setName(name);
        record.setDesignation(designation);
        record.setExtNumber(ext);
        record.setRoom(room);
        record.setMacAddress(mac);
        record.setAssignedAt(LocalDateTime.now());
        return record;
    }
}