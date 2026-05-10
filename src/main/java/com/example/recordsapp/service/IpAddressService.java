package com.example.recordsapp.service;

import com.example.recordsapp.model.IpAddress;
import com.example.recordsapp.repository.IpAddressRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IpAddressService {

    private final IpAddressRepository ipAddressRepository;

    public IpAddressService(IpAddressRepository ipAddressRepository) {
        this.ipAddressRepository = ipAddressRepository;
    }

    public List<IpAddress> findAll() {
        return ipAddressRepository.findAll();
    }

    public Optional<IpAddress> findById(Long id) {
        return ipAddressRepository.findById(id);
    }

    public List<IpAddress> findByAssigned(Boolean assigned) {
        return ipAddressRepository.findByIsAssigned(assigned);
    }

    public Optional<IpAddress> findByIpAddress(String ipAddress) {
        return ipAddressRepository.findByIpAddress(ipAddress);
    }

    public IpAddress save(IpAddress ipAddress) {
        if (ipAddress.getUpdatedAt() == null) {
            ipAddress.setUpdatedAt(LocalDateTime.now());
        }
        return ipAddressRepository.save(ipAddress);
    }

    public void deleteById(Long id) {
        ipAddressRepository.deleteById(id);
    }

    public long count() {
        return ipAddressRepository.count();
    }

    public long countAssigned() {
        return ipAddressRepository.countByIsAssigned(true);
    }

    public long countFree() {
        return ipAddressRepository.countByIsAssigned(false);
    }
}
