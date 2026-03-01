package com.Backend.service;

import com.Backend.dto.GroupMatchDTO;
import com.Backend.model.StudyGroup;
import com.Backend.model.UserProfile;
import com.Backend.model.SubjectPreference;
import com.Backend.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✨ NEW IMPORT

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchingEngineService {

    @Autowired
    private UserProfileRepository userProfileRepo;

    // ✨ NEW: This annotation keeps the DB connection open to fetch the subjects! ✨
    @Transactional
    public GroupMatchDTO calculateMatch(UserProfile userProfile, StudyGroup group) {
        int totalScore = 0;

        String subjectText = "No Match";
        String scheduleText = "Low Overlap";
        String skillText = "Review Needed";
        String overallText;

        try {
            // --- 1. SUBJECT MATCH ---
            boolean subjectMatches = false;
            String userSkillInSubject = "Basic"; 
            
            if (userProfile.getSelectedSubjects() != null && group.getSubject() != null) {
                for (SubjectPreference sp : userProfile.getSelectedSubjects()) {
                    if (sp.getName() != null && sp.getName().trim().equalsIgnoreCase(group.getSubject().trim())) {
                        subjectMatches = true;
                        if (sp.getLevel() != null) {
                            String levelStr = String.valueOf(sp.getLevel()).trim();
                            if (levelStr.equals("1")) userSkillInSubject = "Basic";
                            else if (levelStr.equals("2")) userSkillInSubject = "Intermediate";
                            else if (levelStr.equals("3")) userSkillInSubject = "Advanced";
                            else userSkillInSubject = levelStr; // ✨ FIX: Fallback to raw text
                        }
                        break;
                    }
                }
            }

            if (subjectMatches) {
                totalScore += 40;
                subjectText = "Exact Match";
            } else {
                subjectText = "Different Subject";
            }

            // --- 2. SCHEDULE OVERLAP ---
            int schedulePoints = 0;
            List<String> userDays = userProfile.getSelectedDays();
            List<String> groupDays = group.getSessionDays();
            
            if (userDays != null && groupDays != null && !groupDays.isEmpty()) {
                long matchingDays = groupDays.stream().filter(userDays::contains).count();
                if (matchingDays == groupDays.size()) {
                    schedulePoints += 20;
                    scheduleText = "Perfect Match";
                } else if (matchingDays > 0) {
                    schedulePoints += 10;
                    scheduleText = "Partial Overlap";
                }
                
                if (userProfile.getTimeFrom() != null && group.getSessionTimeFrom() != null) {
                    schedulePoints += 10;
                }
            }
            totalScore += schedulePoints;

            // --- 3. SKILL BALANCE ---
            if (subjectMatches && group.getSkillLevel() != null) {
                if (group.getSkillLevel().equalsIgnoreCase(userSkillInSubject)) {
                    totalScore += 15;
                    skillText = "Peer Learning";
                } else {
                    totalScore += 8; 
                    skillText = "Complementary";
                }
            }

            // --- 4. LEARNING STYLE & SIZE PREFERENCE ---
            if (userProfile.getLearningStyle() != null && group.getLearningStyle() != null) {
                for (String style : group.getLearningStyle()) {
                    if (userProfile.getLearningStyle().contains(style)) {
                        totalScore += 10;
                        break; 
                    }
                }
            }
            
            // Safe Group Size Parsing
            if (userProfile.getGroupSize() != null && group.getMaxCapacity() != null) {
                try {
                    String cleanSizeStr = userProfile.getGroupSize().replaceAll("[^0-9]", " ").trim().split("\\s+")[0];
                    if (!cleanSizeStr.isEmpty()) {
                        int prefSize = Integer.parseInt(cleanSizeStr);
                        int sizeDiff = Math.abs(prefSize - group.getMaxCapacity());
                        if (sizeDiff <= 2) {
                            totalScore += 5;
                        }
                    }
                } catch (Exception e) {}
            }

            // --- FINALIZE OVERALL TEXT ---
            if (totalScore >= 80) overallText = "Highly Recommended";
            else if (totalScore >= 50) overallText = "Good Potential";
            else overallText = "Explore Carefully";

        } catch (Exception e) {
            overallText = "Pending Calculation";
        }

        // --- FETCH REAL SKILLS FOR ALL GROUP MEMBERS ---
        List<GroupMatchDTO.MemberDTO> realMembersList = group.getMembers().stream().map(member -> {
            String role = member.getId().equals(group.getAdmin().getId()) ? "Creator" : "Member";
            String memberSkill = "Unknown"; // Default

            try {
                UserProfile profile = userProfileRepo.findByUserId(member.getId()).orElse(null);
                
                if (profile != null && profile.getSelectedSubjects() != null) {
                    for (SubjectPreference sp : profile.getSelectedSubjects()) {
                        // Check if the member studies the group's subject
                        if (sp.getName() != null && sp.getName().trim().equalsIgnoreCase(group.getSubject().trim())) {
                            String levelStr = String.valueOf(sp.getLevel()).trim();
                            if (levelStr.equals("1")) memberSkill = "Basic";
                            else if (levelStr.equals("2")) memberSkill = "Intermediate";
                            else if (levelStr.equals("3")) memberSkill = "Advanced";
                            else memberSkill = levelStr; // ✨ FIX: Fallback to raw text
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Could not fetch skill for member: " + member.getName());
            }

            return new GroupMatchDTO.MemberDTO(member.getName(), role, memberSkill);
        }).collect(Collectors.toList());

        GroupMatchDTO.Compatibility comp = new GroupMatchDTO.Compatibility(subjectText, skillText, scheduleText, overallText);
        
        return new GroupMatchDTO(group, totalScore, comp, realMembersList);
    }
}