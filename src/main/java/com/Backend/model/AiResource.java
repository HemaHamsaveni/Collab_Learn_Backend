package com.Backend.model;
import jakarta.persistence.*;
import com.Backend.model.enums.ResourceType;

@Entity @Table(name = "ai_resources")
public class AiResource {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne @JoinColumn(name = "group_id") private StudyGroup studyGroup;
    @Enumerated(EnumType.STRING) private ResourceType resourceType;
    private String topic;
    @Column(columnDefinition = "TEXT") private String content;
    // Getters and Setters needed
}