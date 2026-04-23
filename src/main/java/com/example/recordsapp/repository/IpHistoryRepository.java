package com.example.recordsapp.repository;

import com.example.recordsapp.model.IpHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IpHistoryRepository extends JpaRepository<IpHistory, Long> {

    List<IpHistory> findByIpAddressOrderByAssignedAtDesc(String ipAddress);

    List<IpHistory> findByOrderByAssignedAtDesc();
}