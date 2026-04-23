package com.example.recordsapp.service;

import com.example.recordsapp.model.AppUser;
import com.example.recordsapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AppUser> findAll() {
        return userRepository.findAll();
    }

    public Optional<AppUser> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public AppUser save(AppUser user, boolean encodePassword) {
        if (encodePassword && user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public AppUser toggleActive(Long id) {
        Optional<AppUser> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();
            user.setIsActive(!user.getIsActive());
            return userRepository.save(user);
        }
        return null;
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}