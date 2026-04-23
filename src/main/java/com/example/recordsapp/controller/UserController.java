package com.example.recordsapp.controller;

import com.example.recordsapp.model.AppUser;
import com.example.recordsapp.service.UserService;
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
@RequestMapping("/users")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new AppUser());
        model.addAttribute("formAction", "/users");
        model.addAttribute("formTitle", "Add New User");
        return "users/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("user") AppUser user, BindingResult bindingResult, Model model) {
        if (userService.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Username already exists");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/users");
            model.addAttribute("formTitle", "Add New User");
            return "users/form";
        }

        userService.save(user, true);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        AppUser user = userService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id " + id));
        model.addAttribute("user", user);
        model.addAttribute("formAction", "/users/" + id + "/edit");
        model.addAttribute("formTitle", "Edit User");
        return "users/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                    @Valid @ModelAttribute("user") AppUser updatedUser,
                    BindingResult bindingResult,
                    Model model) {
        AppUser existing = userService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id " + id));

        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/users/" + id + "/edit");
            model.addAttribute("formTitle", "Edit User");
            return "users/form";
        }

        existing.setFullName(updatedUser.getFullName());
        existing.setDepartment(updatedUser.getDepartment());
        existing.setRoles(updatedUser.getRoles());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existing.setPassword(updatedUser.getPassword());
            userService.save(existing, true);
        } else {
            userService.save(existing, false);
        }

        return "redirect:/admin";
    }

    @PostMapping("/{id}/toggle")
    public String toggleActive(@PathVariable Long id) {
        userService.toggleActive(id);
        return "redirect:/admin";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}