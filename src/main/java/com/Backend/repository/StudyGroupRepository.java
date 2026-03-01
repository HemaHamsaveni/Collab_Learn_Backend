package com.Backend.repository;

import com.Backend.model.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    // Find groups by subject (e.g., "Math")
    List<StudyGroup> findBySubjectContainingIgnoreCase(String subject);
    
    // Find groups by name (e.g., "Night Owls")
    List<StudyGroup> findByNameContainingIgnoreCase(String name);
}