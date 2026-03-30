package com.Backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Backend.model.StudySession;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    
    // Spring Boot's magic naming convention automatically writes the SQL query for this!
    // It fetches all sessions that belong to a specific group ID.
    List<StudySession> findByStudyGroupId(Long groupId);
}