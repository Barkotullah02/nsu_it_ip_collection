package com.example.recordsapp.controller;

import com.example.recordsapp.model.Department;
import com.example.recordsapp.model.IpAddress;
import com.example.recordsapp.model.IpHistory;
import com.example.recordsapp.model.Record;
import com.example.recordsapp.service.RecordService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping({"/", "/records"})
    public String list(@RequestParam(name = "name", required = false) String name,
                      @RequestParam(name = "department", required = false) String department,
                      @RequestParam(name = "status", required = false) String status,
                      Model model) {
        List<Record> records = recordService.findAll();
        StringBuilder filterInfo = new StringBuilder();

        if (status != null && !status.isBlank()) {
            boolean showActive = "ACTIVE".equalsIgnoreCase(status);
            records = records.stream()
                    .filter(r -> r.isActive() == showActive)
                    .toList();
            filterInfo.append("Status: ").append(status);
        }

        if (name != null && !name.isBlank()) {
            records = records.stream()
                    .filter(r -> r.getName() != null && r.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
            if (filterInfo.length() > 0) filterInfo.append(", ");
            filterInfo.append("Name: ").append(name);
        }

        if (department != null && !department.isBlank()) {
            records = records.stream()
                    .filter(r -> r.getDepartment() != null && r.getDepartment().toLowerCase().contains(department.toLowerCase()))
                    .toList();
            if (filterInfo.length() > 0) filterInfo.append(", ");
            filterInfo.append("Department: ").append(department);
        }

        model.addAttribute("records", records);
        model.addAttribute("departments", recordService.getAllDepartments());
        model.addAttribute("filterInfo", filterInfo.length() > 0 ? filterInfo.toString() : "");
        return "records/list";
    }

    @GetMapping("/records/new")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'EDITOR')")
    public String createForm(Model model) {
        model.addAttribute("record", new Record());
        model.addAttribute("ipAddresses", recordService.findAllIpAddresses());
        model.addAttribute("freeIpAddresses", recordService.findFreeIpAddresses());
        model.addAttribute("departments", recordService.getAllDepartments());
        model.addAttribute("formAction", "/records");
        model.addAttribute("formTitle", "Add New IP Record");
        return "records/form";
    }

    @PostMapping("/records")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'EDITOR')")
    public String create(@RequestParam String ipAddressStr,
                        @RequestParam String departmentName,
                        @Valid @ModelAttribute("record") Record record,
                        BindingResult bindingResult,
                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/records");
            model.addAttribute("formTitle", "Add New IP Record");
            model.addAttribute("ipAddresses", recordService.findAllIpAddresses());
            model.addAttribute("freeIpAddresses", recordService.findFreeIpAddresses());
            model.addAttribute("departments", recordService.getAllDepartments());
            return "records/form";
        }
        try {
            recordService.save(record, ipAddressStr, departmentName);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("formAction", "/records");
            model.addAttribute("formTitle", "Add New IP Record");
            model.addAttribute("ipAddresses", recordService.findAllIpAddresses());
            model.addAttribute("freeIpAddresses", recordService.findFreeIpAddresses());
            model.addAttribute("departments", recordService.getAllDepartments());
            return "records/form";
        }
        return "redirect:/records";
    }

    @GetMapping("/records/{id}")
    public String details(@PathVariable Long id, Model model) {
        Record record = recordService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with id " + id));
        if (record.getIpAddressEntity() != null) {
            List<IpHistory> histories = recordService.findHistoryByIpAddress(record.getIpAddressEntity().getIpAddress());
            model.addAttribute("histories", histories);
        }
        model.addAttribute("record", record);
        return "records/details";
    }

    @GetMapping("/records/{id}/edit")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'EDITOR')")
    public String editForm(@PathVariable Long id, Model model) {
        Record record = recordService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with id " + id));
        model.addAttribute("record", record);
        model.addAttribute("ipAddresses", recordService.findAllIpAddresses());
        model.addAttribute("freeIpAddresses", recordService.findFreeIpAddresses());
        model.addAttribute("departments", recordService.getAllDepartments());
        model.addAttribute("formAction", "/records/" + id + "/edit");
        model.addAttribute("formTitle", "Edit IP Record");
        return "records/form";
    }

    @PostMapping("/records/{id}/edit")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'EDITOR')")
    public String edit(@PathVariable Long id,
                      @RequestParam String departmentName,
                      @Valid @ModelAttribute("record") Record updatedRecord,
                      BindingResult bindingResult,
                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/records/" + id + "/edit");
            model.addAttribute("formTitle", "Edit IP Record");
            model.addAttribute("ipAddresses", recordService.findAllIpAddresses());
            model.addAttribute("freeIpAddresses", recordService.findFreeIpAddresses());
            model.addAttribute("departments", recordService.getAllDepartments());
            return "records/form";
        }

        Record existing = recordService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with id " + id));

        existing.setSl(updatedRecord.getSl());
        existing.setName(updatedRecord.getName());
        existing.setDesignation(updatedRecord.getDesignation());
        existing.setExtNumber(updatedRecord.getExtNumber());
        existing.setMacAddress(updatedRecord.getMacAddress());
        existing.setRoom(updatedRecord.getRoom());
        existing.setUpdatedAt(java.time.LocalDateTime.now());

        Department dept = recordService.getAllDepartmentEntities().stream()
                .filter(d -> d.getName().equalsIgnoreCase(departmentName))
                .findFirst()
                .orElseGet(() -> {
                    Department newDept = new Department();
                    newDept.setName(departmentName);
                    return recordService.saveDepartment(newDept);
                });
        existing.setDepartment(dept);

        recordService.save(existing, existing.getIpAddress(), departmentName);
        return "redirect:/records";
    }

    @PostMapping("/records/{id}/delete")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String delete(@PathVariable Long id) {
        recordService.deleteById(id);
        return "redirect:/records";
    }

    @PostMapping("/records/{id}/free")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'EDITOR')")
    public String markFree(@PathVariable Long id) {
        recordService.markAsFree(id);
        return "redirect:/records";
    }

    @GetMapping("/records/{id}/assign")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'EDITOR')")
    public String assignForm(@PathVariable Long id, Model model) {
        Record record = recordService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with id " + id));
        model.addAttribute("record", record);
        model.addAttribute("departments", recordService.getAllDepartments());
        model.addAttribute("formAction", "/records/" + id + "/assign");
        model.addAttribute("formTitle", "Assign IP to New User");
        return "records/assign";
    }

    @PostMapping("/records/{id}/assign")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'EDITOR')")
    public String assign(@PathVariable Long id,
                      @RequestParam String departmentName,
                      @Valid @ModelAttribute("record") Record newRecord,
                      BindingResult bindingResult,
                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/records/" + id + "/assign");
            model.addAttribute("formTitle", "Assign IP to New User");
            model.addAttribute("departments", recordService.getAllDepartments());
            return "records/assign";
        }

        recordService.assignToNewUser(id, newRecord, departmentName);
        return "redirect:/records";
    }

    @GetMapping("/records/{id}/history")
    public String history(@PathVariable Long id, Model model) {
        Record record = recordService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with id " + id));
        if (record.getIpAddressEntity() != null) {
            List<IpHistory> histories = recordService.findHistoryByIpAddress(record.getIpAddressEntity().getIpAddress());
            model.addAttribute("histories", histories);
        }
        model.addAttribute("record", record);
        return "records/history";
    }

    @GetMapping("/records/export/pdf")
    public String exportPdf(@RequestParam(name = "name", required = false) String name,
                          @RequestParam(name = "department", required = false) String department,
                          @RequestParam(name = "status", required = false) String status,
                          Model model) {
        List<Record> records = recordService.findAll();
        String filterInfo = "";

        if (status != null && !status.isBlank()) {
            boolean showActive = "ACTIVE".equalsIgnoreCase(status);
            records = records.stream()
                    .filter(r -> r.isActive() == showActive)
                    .toList();
            filterInfo = "Status: " + status;
        }

        if (name != null && !name.isBlank()) {
            records = records.stream()
                    .filter(r -> r.getName() != null && r.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
            if (!filterInfo.isEmpty()) filterInfo += ", ";
            filterInfo += "Name: " + name;
        }

        if (department != null && !department.isBlank()) {
            records = records.stream()
                    .filter(r -> r.getDepartment() != null && r.getDepartment().toLowerCase().contains(department.toLowerCase()))
                    .toList();
            if (!filterInfo.isEmpty()) filterInfo += ", ";
            filterInfo += "Department: " + department;
        }

        if (filterInfo.isEmpty()) {
            filterInfo = "All Records";
        }

        model.addAttribute("records", records);
        model.addAttribute("filterInfo", filterInfo);
        model.addAttribute("generatedAt", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return "records/pdf";
    }
}