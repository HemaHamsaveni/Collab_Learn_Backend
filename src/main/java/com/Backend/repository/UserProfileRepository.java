package com.Backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository; // ✨ Make sure to import User!
import org.springframework.stereotype.Repository;

import com.Backend.model.User;
import com.Backend.model.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> 
{
    // Used by your StudyGroupController (searches by the ID number)
    Optional<UserProfile> findByUserId(Long userId);
    
    // ✨ THE FIX: Used by your UserService (searches by the User object)
    Optional<UserProfile> findByUser(User user);
    
}