package com.Backend.service;

import com.Backend.dto.RegisterRequest;
import com.Backend.dto.LoginRequest;
import com.Backend.dto.ProfileUpdateRequest;
import com.Backend.model.User;
import com.Backend.model.UserProfile;
import com.Backend.repository.UserRepository;
import com.Backend.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository; 

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Register
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword())); 
        return userRepository.save(newUser);
    }

    // 2. Login
    public User loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials!");
        }
        return user;
    }

    // 3. Reset Password
    public void resetPasswordByUsername(String username, String newPassword) {
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 4. Update Profile
    @Transactional
    public UserProfile updateUserProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(new UserProfile());
        
        if (profile.getUser() == null) {
            profile.setUser(user);
        }

        if(request.getFullName() != null) profile.setFullName(request.getFullName());
        if(request.getDegree() != null) profile.setDegree(request.getDegree());
        if(request.getSpecialization() != null) profile.setSpecialization(request.getSpecialization());
        if(request.getSemester() != null) profile.setSemester(request.getSemester());
        if(request.getYear() != null) profile.setYear(request.getYear());
        if(request.getLearningStyle() != null) profile.setLearningStyle(request.getLearningStyle());
        if(request.getGroupSize() != null) profile.setGroupSize(request.getGroupSize());
        if(request.getTimeFrom() != null) profile.setTimeFrom(request.getTimeFrom());
        if(request.getTimeTo() != null) profile.setTimeTo(request.getTimeTo());

        if (request.getSelectedSubjects() != null) profile.setSelectedSubjects(request.getSelectedSubjects());
        if (request.getSelectedDays() != null) profile.setSelectedDays(request.getSelectedDays());

        return userProfileRepository.save(profile);
    }

    // 5. Get User Profile & Calculate Streak!
    public UserProfile getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found for this user"));

        // --- STREAK LOGIC ---
        LocalDate today = LocalDate.now();
        LocalDate lastLogin = profile.getLastLoginDate();
        int currentStreak = profile.getStreakCount() != null ? profile.getStreakCount() : 0;

        if (lastLogin == null) {
            profile.setStreakCount(1);
            profile.setLastLoginDate(today);
            userProfileRepository.save(profile);
        } else if (!lastLogin.equals(today)) {
            if (lastLogin.plusDays(1).equals(today)) {
                profile.setStreakCount(currentStreak + 1); // Streak continues!
            } else {
                profile.setStreakCount(1); // Streak resets
            }
            profile.setLastLoginDate(today);
            userProfileRepository.save(profile);
        }

        return profile;
    }
}