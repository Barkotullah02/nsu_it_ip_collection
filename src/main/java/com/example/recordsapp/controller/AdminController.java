package com.example.recordsapp.controller;

import com.example.recordsapp.service.DepartmentService;
import com.example.recordsapp.service.IpAddressService;
import com.example.recordsapp.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final IpAddressService ipAddressService;

    public AdminController(UserService userService, 
                           DepartmentService departmentService,
                           IpAddressService ipAddressService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.ipAddressService = ipAddressService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("userCount", userService.count());
        model.addAttribute("departmentCount", departmentService.count());
        model.addAttribute("totalIpCount", ipAddressService.count());
        model.addAttribute("assignedIpCount", ipAddressService.countAssigned());
        model.addAttribute("freeIpCount", ipAddressService.countFree());
        model.addAttribute("page", "dashboard");
        return "admin";
    }
}