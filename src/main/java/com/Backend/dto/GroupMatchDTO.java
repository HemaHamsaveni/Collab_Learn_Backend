package com.Backend.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.Backend.model.StudyGroup;

public class GroupMatchDTO {
    private Long id;
    private String name;
    private String subject;
    private String level;
    private String score; // e.g., "85%"
    private Compatibility compatibility;
    private String goal;
    private String creationDate;
    private String sessionDetails;
    private int currentMembers;
    private int maxMembers;
    private List<MemberDTO> membersList;

    // --- Inner Classes for Structured Data ---
    public static class Compatibility {
        public String subject;
        public String skill;
        public String schedule;
        public String overall;
        
        public Compatibility(String subject, String skill, String schedule, String overall) {
            this.subject = subject; this.skill = skill; this.schedule = schedule; this.overall = overall;
        }
    }

    public static class MemberDTO {
        public String name;
        public String role;
        public String level;
        
        public MemberDTO(String name, String role, String level) {
            this.name = name; this.role = role; this.level = level;
        }
    }

    // --- Constructor to map a StudyGroup to this DTO ---
    public GroupMatchDTO(StudyGroup group, int matchScore, Compatibility compDetails) {
        this.id = group.getId();
        this.name = group.getName();
        this.subject = group.getSubject();
        this.level = group.getSkillLevel();
        this.score = matchScore + "%";
        this.compatibility = compDetails;
        this.goal = group.getStudyGoal();
        this.currentMembers = group.getMembers().size();
        this.maxMembers = group.getMaxCapacity();
        
        // Format dates and times for the frontend safely
        this.creationDate = group.getCreatedAt() != null ? group.getCreatedAt().toLocalDate().toString() : "Recent";
        
        String daysStr = group.getSessionDays() != null ? String.join(", ", group.getSessionDays()) : "";
        String timeStr = (group.getSessionTimeFrom() != null && group.getSessionTimeTo() != null) 
                         ? group.getSessionTimeFrom() + " - " + group.getSessionTimeTo() : "";
        this.sessionDetails = (!daysStr.isEmpty() && !timeStr.isEmpty()) ? daysStr + " • " + timeStr : "TBD";

        // Map members
        this.membersList = group.getMembers().stream().map(m -> new MemberDTO(
                m.getName(),
                m.getId().equals(group.getAdmin().getId()) ? "Creator" : "Member",
                m.getId().equals(group.getAdmin().getId()) ? group.getSkillLevel() : "Pending"
        )).collect(Collectors.toList());
    }

    // Standard Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSubject() { return subject; }
    public String getLevel() { return level; }
    public String getScore() { return score; }
    public Compatibility getCompatibility() { return compatibility; }
    public String getGoal() { return goal; }
    public String getCreationDate() { return creationDate; }
    public String getSessionDetails() { return sessionDetails; }
    public int getCurrentMembers() { return currentMembers; }
    public int getMaxMembers() { return maxMembers; }
    public List<MemberDTO> getMembersList() { return membersList; }
}