package com.Backend.service;

import com.Backend.dto.GroupMatchDTO;
import com.Backend.model.StudyGroup;
import com.Backend.model.UserProfile;
import com.Backend.model.UserSubject; // Assuming you have this from ProfileSetup
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MatchingEngineService {

    public GroupMatchDTO calculateMatch(UserProfile userProfile, StudyGroup group) {
        int totalScore = 0;

        String subjectText = "No Match";
        String scheduleText = "Low Overlap";
        String skillText = "Review Needed";
        String overallText;

        // --- 1. SUBJECT MATCH (Max 40 Points) ---
        // Based on your database logs, UserProfile has a collection of subjects.
        boolean subjectMatches = false;
        String userSkillInSubject = "Beginner"; // Default
        
        if (userProfile.getSubjects() != null) {
            for (UserSubject us : userProfile.getSubjects()) {
                if (us.getName().equalsIgnoreCase(group.getSubject())) {
                    subjectMatches = true;
                    userSkillInSubject = us.getLevel();
                    break;
                }
            }
        }

        if (subjectMatches) {
            totalScore += 40;
            subjectText = "Exact Match";
        } else {
            // If they don't even study the same subject, it's a very poor match.
            subjectText = "Different Subject";
        }

        // --- 2. SCHEDULE OVERLAP (Max 30 Points) ---
        int schedulePoints = 0;
        Set<String> userDays = userProfile.getStudyDays();
        List<String> groupDays = group.getSessionDays();
        
        if (userDays != null && groupDays != null && !groupDays.isEmpty()) {
            long matchingDays = groupDays.stream().filter(userDays::contains).count();
            if (matchingDays == groupDays.size()) {
                schedulePoints += 20; // All days match
                scheduleText = "Perfect Match";
            } else if (matchingDays > 0) {
                schedulePoints += 10; // Some days match
                scheduleText = "Partial Overlap";
            }
            
            // Time logic (Simplified for MVP: if both have times set, add points. In a real app, do time range overlap)
            if (userProfile.getTimeFrom() != null && group.getSessionTimeFrom() != null) {
                schedulePoints += 10;
            }
        }
        totalScore += schedulePoints;

        // --- 3. SKILL BALANCE (Max 15 Points) ---
        // Only matters if the subjects match
        if (subjectMatches && group.getSkillLevel() != null) {
            if (group.getSkillLevel().equalsIgnoreCase(userSkillInSubject)) {
                totalScore += 15;
                skillText = "Peer Learning (Equal)";
            } else {
                totalScore += 8; // Different levels can still be good for mentoring
                skillText = "Complementary";
            }
        }

        // --- 4. LEARNING STYLE & SIZE PREFERENCE (Max 15 Points) ---
        if (userProfile.getLearningStyle() != null && group.getLearningStyle() != null) {
            if (group.getLearningStyle().contains(userProfile.getLearningStyle())) {
                totalScore += 10;
            }
        }
        
        // Group Size logic (e.g., if User wants a group of 5, and group max capacity is close to 5)
        if (userProfile.getGroupSize() != null && group.getMaxCapacity() != null) {
            int sizeDiff = Math.abs(Integer.parseInt(userProfile.getGroupSize().replace("+","")) - group.getMaxCapacity());
            if (sizeDiff <= 2) {
                totalScore += 5;
            }
        }

        // --- FINALIZE OVERALL TEXT ---
        if (totalScore >= 80) overallText = "Highly Recommended";
        else if (totalScore >= 50) overallText = "Good Potential";
        else overallText = "Explore Carefully";

        // Wrap it all in the DTO
        GroupMatchDTO.Compatibility comp = new GroupMatchDTO.Compatibility(subjectText, skillText, scheduleText, overallText);
        return new GroupMatchDTO(group, totalScore, comp);
    }
}