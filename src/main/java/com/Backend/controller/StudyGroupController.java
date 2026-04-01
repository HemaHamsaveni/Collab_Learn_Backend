package com.Backend.controller;

import java.util.List;
import java.util.stream.Collectors; // ✨ NEW

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin; // ✨ NEW
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping; // ✨ NEW
import org.springframework.web.bind.annotation.PathVariable; // ✨ NEW
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Backend.dto.GroupMatchDTO;
import com.Backend.dto.GroupRequest;
import com.Backend.model.StudyGroup;
import com.Backend.model.User;
import com.Backend.model.UserProfile;
import com.Backend.repository.UserProfileRepository;
import com.Backend.repository.UserRepository;
import com.Backend.service.MatchingEngineService;
import com.Backend.service.StudyGroupService;

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

    // ✨ UPDATED: Now fetches the real skill level for every member
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserGroups(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            
            // Map the groups to a custom List to inject the dynamic "skill" for each member
            List<java.util.Map<String, Object>> response = user.getJoinedGroups().stream().map(group -> {
                java.util.Map<String, Object> groupMap = new java.util.HashMap<>();
                groupMap.put("id", group.getId());
                groupMap.put("name", group.getName());
                groupMap.put("subject", group.getSubject());
                groupMap.put("maxCapacity", group.getMaxCapacity());
                groupMap.put("studyGoal", group.getStudyGoal());
                groupMap.put("skillLevel", group.getSkillLevel());
                groupMap.put("sessionTimeFrom", group.getSessionTimeFrom());
                groupMap.put("sessionTimeTo", group.getSessionTimeTo());
                groupMap.put("createdAt", group.getCreatedAt());
                groupMap.put("sessionDays", group.getSessionDays());
                
                java.util.Map<String, Object> adminMap = new java.util.HashMap<>();
                adminMap.put("id", group.getAdmin().getId());
                adminMap.put("name", group.getAdmin().getName());
                adminMap.put("email", group.getAdmin().getEmail());
                groupMap.put("admin", adminMap);
                
                List<java.util.Map<String, Object>> membersList = group.getMembers().stream().map(member -> {
                    java.util.Map<String, Object> memberMap = new java.util.HashMap<>();
                    memberMap.put("id", member.getId());
                    memberMap.put("name", member.getName());
                    memberMap.put("email", member.getEmail());
                    
                    // ✨ Fetch real skill level from the user's profile
                    String memberSkill = "Unknown";
                    try {
                        com.Backend.model.UserProfile profile = userProfileRepository.findByUserId(member.getId()).orElse(null);
                        if (profile != null && profile.getSelectedSubjects() != null) {
                            for (com.Backend.model.SubjectPreference sp : profile.getSelectedSubjects()) {
                                if (sp.getName() != null && sp.getName().trim().equalsIgnoreCase(group.getSubject().trim())) {
                                    String levelStr = String.valueOf(sp.getLevel()).trim();
                                    if (levelStr.equals("1")) memberSkill = "Basic";
                                    else if (levelStr.equals("2")) memberSkill = "Intermediate";
                                    else if (levelStr.equals("3")) memberSkill = "Advanced";
                                    else memberSkill = levelStr;
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {}
                    
                    memberMap.put("skill", memberSkill); // Inject the real skill!
                    return memberMap;
                }).collect(Collectors.toList());
                
                groupMap.put("members", membersList);
                return groupMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
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