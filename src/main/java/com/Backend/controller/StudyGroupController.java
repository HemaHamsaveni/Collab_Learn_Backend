package com.Backend.controller;

import com.Backend.dto.GroupRequest;
import com.Backend.model.StudyGroup;
import com.Backend.model.User;
import com.Backend.repository.UserRepository;
import com.Backend.service.StudyGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:5173")
public class StudyGroupController {

    @Autowired
    private StudyGroupService groupService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody GroupRequest request) {
        try {
            StudyGroup group = groupService.createGroup(request);
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudyGroup>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    // ✨ NEW: Get groups for a specific user
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

    // ✨ NEW: Leave Group Endpoint
    @DeleteMapping("/{groupId}/leave/{userId}")
    public ResponseEntity<?> leaveGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        try {
            groupService.leaveGroup(groupId, userId);
            return ResponseEntity.ok("Successfully left the group.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // ✨ NEW: Delete Group Endpoint
    @DeleteMapping("/{groupId}/delete/{userId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        try {
            groupService.deleteGroup(groupId, userId);
            return ResponseEntity.ok("Group deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // ✨ NEW: Dynamic Search Endpoint
    @GetMapping("/search")
    public ResponseEntity<List<StudyGroup>> searchGroups(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String skillLevel,
            @RequestParam(required = false) String studyGoal,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) List<String> days) {
        
        return ResponseEntity.ok(groupService.searchGroups(subject, skillLevel, studyGoal, size, days));
    }
}