package com.Backend.dto;

public class AiRequestDTO {
    private String subject;
    private String skillLevel;
    private String topic;
    private String materialType; // e.g., "flashcards" or "study_guide"

    // --- Getters and Setters ---
    
    public String getSubject() { 
        return subject; 
    }
    
    public void setSubject(String subject) { 
        this.subject = subject; 
    }
    
    public String getSkillLevel() { 
        return skillLevel; 
    }
    
    public void setSkillLevel(String skillLevel) { 
        this.skillLevel = skillLevel; 
    }
    
    public String getTopic() { 
        return topic; 
    }
    
    public void setTopic(String topic) { 
        this.topic = topic; 
    }
    
    public String getMaterialType() { 
        return materialType; 
    }
    
    public void setMaterialType(String materialType) { 
        this.materialType = materialType; 
    }
}