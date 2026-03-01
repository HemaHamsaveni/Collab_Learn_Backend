package com.Backend.repository;

import com.Backend.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    // Find all availability slots for a specific user
    List<Availability> findByUserId(Long userId);
}