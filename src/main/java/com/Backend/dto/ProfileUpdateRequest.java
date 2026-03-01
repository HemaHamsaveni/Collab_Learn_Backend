package com.Backend.dto;

import com.Backend.model.SubjectPreference;
import java.util.List;

public class ProfileUpdateRequest {
    
    private String fullName;
    private String degree;
    private String specialization;
    private String semester;
    private String year; // Matches UserProfile
    private String learningStyle;
    private String groupSize; // Matches UserProfile
    private String timeFrom; // Matches UserProfile
    private String timeTo; // Matches UserProfile
    
    private List<String> selectedDays; // Matches UserProfile array
    private List<SubjectPreference> selectedSubjects; // Matches UserProfile objects

    // --- GETTERS AND SETTERS ---
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

    public List<String> getSelectedDays() { return selectedDays; }
    public void setSelectedDays(List<String> selectedDays) { this.selectedDays = selectedDays; }

    public List<SubjectPreference> getSelectedSubjects() { return selectedSubjects; }
    public void setSelectedSubjects(List<SubjectPreference> selectedSubjects) { this.selectedSubjects = selectedSubjects; }
}