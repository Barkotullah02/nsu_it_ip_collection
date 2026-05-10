package com.example.recordsapp.controller;

import com.example.recordsapp.model.IpAddress;
import com.example.recordsapp.service.IpAddressService;
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
@RequestMapping("/admin/ip-addresses")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminIpAddressController {

    private final IpAddressService ipAddressService;

    public AdminIpAddressController(IpAddressService ipAddressService) {
        this.ipAddressService = ipAddressService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("ipAddresses", ipAddressService.findAll());
        model.addAttribute("totalCount", ipAddressService.count());
        model.addAttribute("assignedCount", ipAddressService.countAssigned());
        model.addAttribute("freeCount", ipAddressService.countFree());
        model.addAttribute("page", "ip-addresses");
        return "admin/ip-addresses/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("ipAddress", new IpAddress());
        model.addAttribute("formAction", "/admin/ip-addresses");
        model.addAttribute("formTitle", "Add New IP Address");
        return "admin/ip-addresses/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("ipAddress") IpAddress ipAddress, BindingResult bindingResult, Model model) {
        if (ipAddressService.findByIpAddress(ipAddress.getIpAddress()).isPresent()) {
            bindingResult.rejectValue("ipAddress", "error.ipaddress", "IP Address already exists");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/admin/ip-addresses");
            model.addAttribute("formTitle", "Add New IP Address");
            return "admin/ip-addresses/form";
        }

        ipAddressService.save(ipAddress);
        return "redirect:/admin/ip-addresses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        IpAddress ipAddress = ipAddressService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("IP Address not found with id " + id));
        model.addAttribute("ipAddress", ipAddress);
        model.addAttribute("formAction", "/admin/ip-addresses/" + id + "/edit");
        model.addAttribute("formTitle", "Edit IP Address");
        return "admin/ip-addresses/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("ipAddress") IpAddress updatedIpAddress,
                       BindingResult bindingResult,
                       Model model) {
        IpAddress existing = ipAddressService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("IP Address not found with id " + id));

        if (!existing.getIpAddress().equals(updatedIpAddress.getIpAddress()) && 
            ipAddressService.findByIpAddress(updatedIpAddress.getIpAddress()).isPresent()) {
            bindingResult.rejectValue("ipAddress", "error.ipaddress", "IP Address already exists");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/admin/ip-addresses/" + id + "/edit");
            model.addAttribute("formTitle", "Edit IP Address");
            return "admin/ip-addresses/form";
        }

        existing.setIpAddress(updatedIpAddress.getIpAddress());
        existing.setMacAddress(updatedIpAddress.getMacAddress());
        existing.setRoom(updatedIpAddress.getRoom());
        existing.setIsAssigned(updatedIpAddress.getIsAssigned());
        ipAddressService.save(existing);
        return "redirect:/admin/ip-addresses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        ipAddressService.deleteById(id);
        return "redirect:/admin/ip-addresses";
    }
}
