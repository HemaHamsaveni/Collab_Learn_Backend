package com.Backend.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.Backend.model.enums.SessionStatus;

@Entity @Table(name = "study_sessions")
public class StudySession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne @JoinColumn(name = "group_id") private StudyGroup studyGroup;
    private String topic;
    private LocalDateTime scheduledTime;
    private String meetingLink;
    @Enumerated(EnumType.STRING) private SessionStatus status;
    // Getters and Setters needed
}