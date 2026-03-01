package com.Backend.service;

import com.Backend.dto.GroupRequest;
import com.Backend.model.StudyGroup;
import com.Backend.model.User;
import com.Backend.repository.StudyGroupRepository;
import com.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        group.setDescription(request.getDescription());
        group.setMaxCapacity(request.getMaxCapacity());
        group.setAdmin(admin);
        
        // Admin is automatically the first member
        group.getMembers().add(admin);

        return groupRepository.save(group);
    }

    // 2. Get all groups
    public List<StudyGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    // 3. JOIN A GROUP LOGIC
    public StudyGroup joinGroup(Long groupId, Long userId) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if already a member
        if (group.getMembers().contains(user)) {
            throw new RuntimeException("You are already a member of this group!");
        }

        // Check capacity
        if (group.getMembers().size() >= group.getMaxCapacity()) {
            throw new RuntimeException("Sorry, this group is full!");
        }

        // Add user to group
        group.getMembers().add(user);
        return groupRepository.save(group);
    }
}