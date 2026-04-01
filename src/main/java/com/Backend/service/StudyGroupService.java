package com.Backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Backend.dto.GroupRequest;
import com.Backend.model.StudyGroup;
import com.Backend.model.User;
import com.Backend.repository.StudyGroupRepository;
import com.Backend.repository.UserRepository;

@Service
public class StudyGroupService {

    @Autowired
    private StudyGroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Create a new group
    public StudyGroup createGroup(GroupRequest request) {
        User admin = userRepository.findById(request.getAdminId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        StudyGroup group = new StudyGroup();
        group.setName(request.getName());
        group.setSubject(request.getSubject());
        group.setMaxCapacity(request.getMembersCount());
        group.setStudyGoal(request.getStudyGoal());
        group.setSkillLevel(request.getSkillLevel());
        group.setLearningStyle(request.getLearningStyle());
        group.setSessionDays(request.getSessionDays());
        group.setSessionTimeFrom(request.getSessionTimeFrom());
        group.setSessionTimeTo(request.getSessionTimeTo());
        group.setAdmin(admin);
        
        group.getMembers().add(admin);
        return groupRepository.save(group);
    }

    // 2. Get all groups
    public List<StudyGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    // 3. Join Group
    public StudyGroup joinGroup(Long groupId, Long userId) {
        StudyGroup group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found!"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
        if (group.getMembers().contains(user)) throw new RuntimeException("Already a member!");
        if (group.getMembers().size() >= group.getMaxCapacity()) throw new RuntimeException("Group is full!");
        
        group.getMembers().add(user);
        return groupRepository.save(group);
    }

    // 4. Leave Group
    public void leaveGroup(Long groupId, Long userId) {
        StudyGroup group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found!"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
        
        if (!group.getMembers().contains(user)) {
            throw new RuntimeException("You are not a member of this group!");
        }
        
        if (group.getAdmin().getId().equals(userId)) {
            throw new RuntimeException("Admin cannot leave the group. You must delete it or transfer ownership first.");
        }

        group.getMembers().remove(user);
        groupRepository.save(group);
    }

    // 5. Delete Group (For Creators Only)
    public void deleteGroup(Long groupId, Long userId) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found!"));
        
        // Verify the user is the admin/creator
        if (!group.getAdmin().getId().equals(userId)) {
            throw new RuntimeException("Permission denied. Only the group creator can delete this group.");
        }

        // Delete the group (Hibernate will automatically remove the links in the group_members join table)
        groupRepository.delete(group);
    }

    // ✨ NEW: 6. Transfer Group Ownership
    public StudyGroup transferOwnership(Long groupId, Long currentAdminId, Long newAdminId) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found!"));

        // 1. Verify the person requesting the transfer is the actual admin
        if (!group.getAdmin().getId().equals(currentAdminId)) {
            throw new RuntimeException("Permission denied. Only the current creator can transfer ownership.");
        }

        // 2. Find the new admin
        User newAdmin = userRepository.findById(newAdminId)
                .orElseThrow(() -> new RuntimeException("Target user not found!"));

        // 3. Ensure the new admin is actually in the group
        if (!group.getMembers().contains(newAdmin)) {
            throw new RuntimeException("The new leader must be an existing member of the group.");
        }

        // 4. Transfer power and save
        group.setAdmin(newAdmin);
        return groupRepository.save(group);
    }

    // 7. Dynamic Search Service
    public List<StudyGroup> searchGroups(String subject, String skillLevel, String studyGoal, String size, List<String> days) {
        
        // 1. Get basic matches from the database
        List<StudyGroup> groups = groupRepository.searchByFilters(subject, skillLevel, studyGoal);

        // 2. Filter complex logic (Size and Days) using Java Streams
        return groups.stream().filter(group -> {
            
            // Check Group Size
            if (size != null && !size.isEmpty() && !size.equals("Any")) {
                if (size.equals("10+")) {
                    if (group.getMaxCapacity() < 10) return false;
                } else {
                    if (group.getMaxCapacity() != Integer.parseInt(size)) return false;
                }
            }

            // Check Preferred Days (Keep group if it has AT LEAST ONE day matching the user's selected days)
            if (days != null && !days.isEmpty()) {
                if (group.getSessionDays() == null || group.getSessionDays().isEmpty()) {
                    return false; // Group hasn't set days, filter it out
                }
                // Check for intersection
                boolean hasMatchingDay = false;
                for (String day : days) {
                    if (group.getSessionDays().contains(day)) {
                        hasMatchingDay = true;
                        break;
                    }
                }
                if (!hasMatchingDay) return false;
            }

            return true;
        }).toList();
    }
}