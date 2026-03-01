package com.Backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Backend.model.User;
import com.Backend.model.UserProfile;

public interface UserRepository extends JpaRepository<User, Long> {
Optional<User> findByEmail(String email);
    
    Optional<User> findByName(String name); 
    
    boolean existsByEmail(String email);
}