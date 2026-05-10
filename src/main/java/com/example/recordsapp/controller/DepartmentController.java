package com.example.recordsapp.controller;

import com.example.recordsapp.model.Department;
import com.example.recordsapp.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/departments")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("page", "departments");
        return "admin/departments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("formAction", "/admin/departments");
        model.addAttribute("formTitle", "Add New Department");
        return "admin/departments/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("department") Department department, BindingResult bindingResult, Model model) {
        if (departmentService.existsByName(department.getName())) {
            bindingResult.rejectValue("name", "error.department", "Department name already exists");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/admin/departments");
            model.addAttribute("formTitle", "Add New Department");
            return "admin/departments/form";
        }

        departmentService.save(department);
        return "redirect:/admin/departments";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Department department = departmentService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Department not found with id " + id));
        model.addAttribute("department", department);
        model.addAttribute("formAction", "/admin/departments/" + id + "/edit");
        model.addAttribute("formTitle", "Edit Department");
        return "admin/departments/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("department") Department updatedDepartment,
                       BindingResult bindingResult,
                       Model model) {
        Department existing = departmentService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Department not found with id " + id));

        if (!existing.getName().equalsIgnoreCase(updatedDepartment.getName()) && 
            departmentService.existsByName(updatedDepartment.getName())) {
            bindingResult.rejectValue("name", "error.department", "Department name already exists");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/admin/departments/" + id + "/edit");
            model.addAttribute("formTitle", "Edit Department");
            return "admin/departments/form";
        }

        existing.setName(updatedDepartment.getName());
        existing.setDescription(updatedDepartment.getDescription());
        departmentService.save(existing);
        return "redirect:/admin/departments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        departmentService.deleteById(id);
        return "redirect:/admin/departments";
    }
}
