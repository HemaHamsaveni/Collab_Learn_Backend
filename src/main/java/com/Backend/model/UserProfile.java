package com.Backend.model;

import jakarta.persistence.*;
import java.time.LocalDate; // ✨ ADDED THIS IMPORT
import java.util.List;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // This links directly to your existing User table!
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String fullName;
    private String degree;
    private String specialization;
    private String semester;
    private String year;
    private String learningStyle;
    private String groupSize;
    private String timeFrom;
    private String timeTo;

    // ✨ NEW: STREAK TRACKING VARIABLES ✨
    private Integer streakCount = 0; 
    private LocalDate lastLoginDate; 

    // Stores the ['Mon', 'Wed', 'Fri'] array from React
    @ElementCollection
    @CollectionTable(name = "user_study_days", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "day")
    private List<String> selectedDays;

    // Stores the [{name: 'Java', level: 2}] objects from React
    @ElementCollection
    @CollectionTable(name = "user_subjects", joinColumns = @JoinColumn(name = "profile_id"))
    private List<SubjectPreference> selectedSubjects;

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    
    public String getLearningStyle() { return learningStyle; }
    public void setLearningStyle(String learningStyle) { this.learningStyle = learningStyle; }
    
    public String getGroupSize() { return groupSize; }
    public void setGroupSize(String groupSize) { this.groupSize = groupSize; }
    
    public String getTimeFrom() { return timeFrom; }
    public void setTimeFrom(String timeFrom) { this.timeFrom = timeFrom; }
    
    public String getTimeTo() { return timeTo; }
    public void setTimeTo(String timeTo) { this.timeTo = timeTo; }

    // ✨ NEW: STREAK GETTERS AND SETTERS ✨
    public Integer getStreakCount() { return streakCount; }
    public void setStreakCount(Integer streakCount) { this.streakCount = streakCount; }
    
    public LocalDate getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDate lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public List<String> getSelectedDays() { return selectedDays; }
    public void setSelectedDays(List<String> selectedDays) { this.selectedDays = selectedDays; }
    
    public List<SubjectPreference> getSelectedSubjects() { return selectedSubjects; }
    public void setSelectedSubjects(List<SubjectPreference> selectedSubjects) { this.selectedSubjects = selectedSubjects; }
}