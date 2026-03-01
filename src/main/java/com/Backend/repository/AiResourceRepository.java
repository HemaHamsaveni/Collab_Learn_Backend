package com.Backend.repository;

import com.Backend.model.AiResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AiResourceRepository extends JpaRepository<AiResource, Long> {
    // Get all AI resources (flashcards/summaries) for a group
    List<AiResource> findByStudyGroupId(Long groupId);
}