package com.example.recordsapp.controller;

import com.example.recordsapp.model.IpAddress;
import com.example.recordsapp.service.RecordService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ip-addresses")
public class IpAddressController {

    private final RecordService recordService;

    public IpAddressController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping
    public String list(Model model) {
        List<IpAddress> allIps = recordService.findAllIpAddresses();
        List<IpAddress> freeIps = recordService.findFreeIpAddresses();
        List<IpAddress> assignedIps = recordService.findAssignedIpAddresses();
        
        model.addAttribute("allIps", allIps);
        model.addAttribute("freeIps", freeIps);
        model.addAttribute("assignedIps", assignedIps);
        return "ip-addresses/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("ipAddress", new IpAddress());
        return "ip-addresses/form";
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String create(@ModelAttribute IpAddress ipAddress) {
        recordService.saveIpAddress(ipAddress);
        return "redirect:/ip-addresses";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String delete(@PathVariable Long id) {
        recordService.deleteIpAddressById(id);
        return "redirect:/ip-addresses";
    }
}