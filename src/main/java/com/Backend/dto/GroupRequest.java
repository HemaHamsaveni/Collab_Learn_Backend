package com.Backend.dto;
import java.util.List;

public class GroupRequest {
    private String name;
    private String subject;
    private Integer membersCount; // Replaces maxCapacity from frontend
    private String studyGoal;
    private String skillLevel;
    private List<String> learningStyle;
    private List<String> sessionDays; // Replaces createDays
    private String sessionTimeFrom;
    private String sessionTimeTo;
    private Long adminId; 

    // --- GETTERS AND SETTERS ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public Integer getMembersCount() { return membersCount; }
    public void setMembersCount(Integer membersCount) { this.membersCount = membersCount; }
    public String getStudyGoal() { return studyGoal; }
    public void setStudyGoal(String studyGoal) { this.studyGoal = studyGoal; }
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public List<String> getLearningStyle() { return learningStyle; }
    public void setLearningStyle(List<String> learningStyle) { this.learningStyle = learningStyle; }
    public List<String> getSessionDays() { return sessionDays; }
    public void setSessionDays(List<String> sessionDays) { this.sessionDays = sessionDays; }
    public String getSessionTimeFrom() { return sessionTimeFrom; }
    public void setSessionTimeFrom(String sessionTimeFrom) { this.sessionTimeFrom = sessionTimeFrom; }
    public String getSessionTimeTo() { return sessionTimeTo; }
    public void setSessionTimeTo(String sessionTimeTo) { this.sessionTimeTo = sessionTimeTo; }
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
}