package com.Backend.controller;

import com.Backend.dto.GroupRequest;
import com.Backend.model.StudyGroup;
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

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody GroupRequest request) {
        try {
            StudyGroup group = groupService.createGroup(request);
            return ResponseEntity.ok("Group '" + group.getName() + "' created successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudyGroup>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    // NEW: Endpoint to Join a Group!
    @PostMapping("/{groupId}/join/{userId}")
    public ResponseEntity<?> joinGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        try {
            StudyGroup updatedGroup = groupService.joinGroup(groupId, userId);
            return ResponseEntity.ok("Successfully joined: " + updatedGroup.getName());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}