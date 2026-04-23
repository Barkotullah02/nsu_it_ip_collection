package com.example.recordsapp.controller;

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
                      Model model) {
        List<Record> records;

        if (name != null && !name.isBlank()) {
            records = recordService.findByNameContaining(name);
        } else if (department != null && !department.isBlank()) {
            records = recordService.findByDepartmentContaining(department);
        } else {
            records = recordService.findAll();
        }

        model.addAttribute("records", records);
        model.addAttribute("departments", recordService.getAllDepartments());
        return "records/list";
    }

    @GetMapping("/records/new")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'EDITOR')")
    public String createForm(Model model) {
        model.addAttribute("record", new Record());
        model.addAttribute("formAction", "/records");
        model.addAttribute("formTitle", "Add New IP Record");
        return "records/form";
    }

    @PostMapping("/records")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'EDITOR')")
    public String create(@Valid @ModelAttribute("record") Record record, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/records");
            model.addAttribute("formTitle", "Add New IP Record");
            return "records/form";
        }
        recordService.save(record);
        return "redirect:/records";
    }

    @GetMapping("/records/{id}")
    public String details(@PathVariable Long id, Model model) {
        Record record = recordService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Record not found with id " + id));
        model.addAttribute("record", record);
        return "records/details";
    }

    @GetMapping("/records/{id}/edit")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'EDITOR')")
    public String editForm(@PathVariable Long id, Model model) {
        Record record = recordService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Record not found with id " + id));
        model.addAttribute("record", record);
        model.addAttribute("formAction", "/records/" + id + "/edit");
        model.addAttribute("formTitle", "Edit IP Record");
        return "records/form";
    }

    @PostMapping("/records/{id}/edit")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'EDITOR')")
    public String edit(@PathVariable Long id,
                      @Valid @ModelAttribute("record") Record updatedRecord,
                      BindingResult bindingResult,
                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/records/" + id + "/edit");
            model.addAttribute("formTitle", "Edit IP Record");
            return "records/form";
        }

        Record existing = recordService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Record not found with id " + id));

        existing.setSl(updatedRecord.getSl());
        existing.setName(updatedRecord.getName());
        existing.setDesignation(updatedRecord.getDesignation());
        existing.setExtNumber(updatedRecord.getExtNumber());
        existing.setIpAddress(updatedRecord.getIpAddress());
        existing.setMacAddress(updatedRecord.getMacAddress());
        existing.setRoom(updatedRecord.getRoom());
        existing.setDepartment(updatedRecord.getDepartment());

        recordService.save(existing);
        return "redirect:/records";
    }

    @PostMapping("/records/{id}/delete")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String delete(@PathVariable Long id) {
        recordService.deleteById(id);
        return "redirect:/records";
    }
}