package com.example.recordsapp.config;

import com.example.recordsapp.model.AppUser;
import com.example.recordsapp.model.Role;
import com.example.recordsapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
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
        };
    }
}