package com.example.recordsapp.controller;

import com.example.recordsapp.model.Department;
import com.example.recordsapp.model.IpAddress;
import com.example.recordsapp.service.RecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final RecordService recordService;

    public ApiController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/ip-addresses")
    public ResponseEntity<List<Map<String, String>>> getIpAddresses(@RequestParam(required = false) String query) {
        List<IpAddress> allIps = recordService.findAllIpAddresses();
        
        List<Map<String, String>> result = new ArrayList<>();
        for (IpAddress ip : allIps) {
            if (query == null || query.isBlank() || 
                ip.getIpAddress().contains(query) || 
                (ip.getRoom() != null && ip.getRoom().toLowerCase().contains(query.toLowerCase()))) {
                Map<String, String> item = new HashMap<>();
                item.put("ip", ip.getIpAddress());
                item.put("room", ip.getRoom() != null ? ip.getRoom() : "");
                item.put("assigned", ip.getIsAssigned().toString());
                result.add(item);
            }
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Map<String, Object>>> getDepartments(@RequestParam(required = false) String query) {
        List<Department> allDepts = recordService.getAllDepartmentEntities();
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Department dept : allDepts) {
            if (query == null || query.isBlank() || 
                dept.getName().toLowerCase().contains(query.toLowerCase())) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", dept.getId());
                item.put("name", dept.getName());
                item.put("description", dept.getDescription() != null ? dept.getDescription() : "");
                result.add(item);
            }
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/free-ip-addresses")
    public ResponseEntity<List<Map<String, String>>> getFreeIpAddresses(@RequestParam(required = false) String query) {
        List<IpAddress> freeIps = recordService.findFreeIpAddresses();
        
        List<Map<String, String>> result = new ArrayList<>();
        for (IpAddress ip : freeIps) {
            if (query == null || query.isBlank() || 
                ip.getIpAddress().contains(query) || 
                (ip.getRoom() != null && ip.getRoom().toLowerCase().contains(query.toLowerCase()))) {
                Map<String, String> item = new HashMap<>();
                item.put("ip", ip.getIpAddress());
                item.put("room", ip.getRoom() != null ? ip.getRoom() : "");
                result.add(item);
            }
        }
        
        return ResponseEntity.ok(result);
    }
}