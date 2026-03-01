package com.Backend.repository;

import com.Backend.model.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    
    List<StudyGroup> findBySubjectContainingIgnoreCase(String subject);
    List<StudyGroup> findByNameContainingIgnoreCase(String name);

    // ✨ NEW: Dynamic Search Query for Filters
    @Query("SELECT g FROM StudyGroup g WHERE " +
           "(:subject IS NULL OR :subject = '' OR :subject = 'Any' OR g.subject = :subject) AND " +
           "(:skillLevel IS NULL OR :skillLevel = '' OR :skillLevel = 'Any' OR g.skillLevel = :skillLevel) AND " +
           "(:studyGoal IS NULL OR :studyGoal = '' OR :studyGoal = 'Any' OR g.studyGoal = :studyGoal)")
    List<StudyGroup> searchByFilters(
            @Param("subject") String subject,
            @Param("skillLevel") String skillLevel,
            @Param("studyGoal") String studyGoal);
}