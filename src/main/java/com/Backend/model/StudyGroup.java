package com.Backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "study_groups")
public class StudyGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String subject; 
    private String description;
    private Integer maxCapacity;
    
    // ✨ NEW FIELDS FROM FRONTEND ✨
    private String studyGoal;
    private String skillLevel;
    private String sessionTimeFrom;
    private String sessionTimeTo;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @ElementCollection
    @CollectionTable(name = "group_learning_styles", joinColumns = @JoinColumn(name = "group_id"))
    private List<String> learningStyle;

    @ElementCollection
    @CollectionTable(name = "group_session_days", joinColumns = @JoinColumn(name = "group_id"))
    private List<String> sessionDays;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonIgnoreProperties({"joinedGroups", "availabilities", "passwordHash"}) 
    private User admin;
    
    @ManyToMany
    @JoinTable(
        name = "group_members", 
        joinColumns = @JoinColumn(name = "group_id"), 
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties({"joinedGroups", "availabilities", "passwordHash"}) 
    private Set<User> members = new HashSet<>();

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }
    public String getStudyGoal() { return studyGoal; }
    public void setStudyGoal(String studyGoal) { this.studyGoal = studyGoal; }
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public String getSessionTimeFrom() { return sessionTimeFrom; }
    public void setSessionTimeFrom(String sessionTimeFrom) { this.sessionTimeFrom = sessionTimeFrom; }
    public String getSessionTimeTo() { return sessionTimeTo; }
    public void setSessionTimeTo(String sessionTimeTo) { this.sessionTimeTo = sessionTimeTo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<String> getLearningStyle() { return learningStyle; }
    public void setLearningStyle(List<String> learningStyle) { this.learningStyle = learningStyle; }
    public List<String> getSessionDays() { return sessionDays; }
    public void setSessionDays(List<String> sessionDays) { this.sessionDays = sessionDays; }
    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }
    public Set<User> getMembers() { return members; }
    public void setMembers(Set<User> members) { this.members = members; }
}