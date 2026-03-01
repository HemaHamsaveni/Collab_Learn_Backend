package com.Backend.repository;

import com.Backend.model.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    // Get all sessions for a specific group
    List<StudySession> findByStudyGroupId(Long groupId);
}