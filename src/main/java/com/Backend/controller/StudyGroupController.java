package com.Backend.controller;

import com.Backend.dto.GroupRequest;
import com.Backend.dto.GroupMatchDTO; // ✨ NEW
import com.Backend.model.StudyGroup;
import com.Backend.model.User;
import com.Backend.model.UserProfile; // ✨ NEW
import com.Backend.repository.UserRepository;
import com.Backend.repository.UserProfileRepository; // ✨ NEW
import com.Backend.service.MatchingEngineService; // ✨ NEW
import com.Backend.service.StudyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:5173")
public class StudyGroupController {

    @Autowired
    private StudyGroupService groupService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository; // ✨ NEW

    @Autowired
    private MatchingEngineService matchingService; // ✨ NEW

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody GroupRequest request) {
        try {
            StudyGroup group = groupService.createGroup(request);
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✨ UPDATED: Now requires userId to calculate scores ✨
    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllGroupsWithScores(@PathVariable Long userId) {
        try {
            // 1. Fetch user profile
            UserProfile userProfile = userProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found."));

            // 2. Fetch all groups
            List<StudyGroup> allGroups = groupService.getAllGroups();

            // 3. Score and sort groups
            List<GroupMatchDTO> scoredGroups = allGroups.stream()
                    .map(group -> matchingService.calculateMatch(userProfile, group))
                    .sorted((g1, g2) -> {
                        int score1 = Integer.parseInt(g1.getScore().replace("%", ""));
                        int score2 = Integer.parseInt(g2.getScore().replace("%", ""));
                        return Integer.compare(score2, score1); // Sort Highest to Lowest
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(scoredGroups);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserGroups(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(user.getJoinedGroups());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{groupId}/join/{userId}")
    public ResponseEntity<?> joinGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        try {
            StudyGroup updatedGroup = groupService.joinGroup(groupId, userId);
            return ResponseEntity.ok(updatedGroup);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{groupId}/leave/{userId}")
    public ResponseEntity<?> leaveGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        try {
            groupService.leaveGroup(groupId, userId);
            return ResponseEntity.ok("Successfully left the group.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{groupId}/delete/{userId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        try {
            groupService.deleteGroup(groupId, userId);
            return ResponseEntity.ok("Group deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✨ UPDATED: Now requires userId to calculate scores on filtered results ✨
    @GetMapping("/search/{userId}")
    public ResponseEntity<?> searchGroupsWithScores(
            @PathVariable Long userId,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String skillLevel,
            @RequestParam(required = false) String studyGoal,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) List<String> days) {
        
        try {
            UserProfile userProfile = userProfileRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found."));

            List<StudyGroup> searchResults = groupService.searchGroups(subject, skillLevel, studyGoal, size, days);

            List<GroupMatchDTO> scoredResults = searchResults.stream()
                    .map(group -> matchingService.calculateMatch(userProfile, group))
                    .sorted((g1, g2) -> {
                        int score1 = Integer.parseInt(g1.getScore().replace("%", ""));
                        int score2 = Integer.parseInt(g2.getScore().replace("%", ""));
                        return Integer.compare(score2, score1);
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(scoredResults);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}