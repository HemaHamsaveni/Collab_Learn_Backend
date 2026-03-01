package com.Backend.dto;

public class GroupRequest 
{
    private String name;
    private String subject;
    private String description;
    private Integer maxCapacity;
    private Long adminId; // The ID of the user creating the group

    // --- GETTERS AND SETTERS ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
}