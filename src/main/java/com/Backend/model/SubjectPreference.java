package com.Backend.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class SubjectPreference {
    private String name;
    private Integer level;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
}