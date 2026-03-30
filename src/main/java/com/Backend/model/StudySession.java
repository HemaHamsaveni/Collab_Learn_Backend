package com.Backend.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.Backend.model.enums.SessionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "study_sessions")
public class StudySession {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    @ManyToOne 
    @JoinColumn(name = "group_id") 
    // ✨ REMOVED "admin" FROM HERE SO REACT KNOWS WHO THE CREATOR IS!
    @JsonIgnoreProperties({"members", "joinedGroups"}) 
    private StudyGroup studyGroup;
    
    private String topic;
    private LocalDateTime scheduledTime;
    private String meetingLink;
    
    @Enumerated(EnumType.STRING) 
    private SessionStatus status;

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    // ✨ NEW: Tracks everyone who clicked the "Join" button
    @ManyToMany
    @JoinTable(
        name = "session_attendees",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties({"joinedGroups", "availabilities", "passwordHash"})
    private Set<User> attendees = new HashSet<>();

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudyGroup getStudyGroup() { return studyGroup; }
    public void setStudyGroup(StudyGroup studyGroup) { this.studyGroup = studyGroup; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public Set<User> getAttendees() { return attendees; }
    public void setAttendees(Set<User> attendees) { this.attendees = attendees; }
}