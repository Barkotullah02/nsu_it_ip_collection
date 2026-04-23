package com.example.recordsapp.repository;

import com.example.recordsapp.model.IpAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IpAddressRepository extends JpaRepository<IpAddress, Long> {
    Optional<IpAddress> findByIpAddress(String ipAddress);
    List<IpAddress> findByIsAssigned(Boolean isAssigned);
    boolean existsByIpAddress(String ipAddress);
}