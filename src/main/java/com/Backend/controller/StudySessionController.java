package com.Backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Backend.model.StudySession;
import com.Backend.service.StudySessionService;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "http://localhost:5173")
public class StudySessionController {

    @Autowired
    private StudySessionService sessionService;

    // 1. Schedule a new session for a group
    @PostMapping("/group/{groupId}")
    public ResponseEntity<?> createSession(@PathVariable Long groupId, @RequestBody StudySession session) {
        try {
            StudySession newSession = sessionService.createSession(groupId, session);
            return ResponseEntity.ok(newSession);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. Fetch all sessions for a specific group
    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupSessions(@PathVariable Long groupId) {
        try {
            List<StudySession> sessions = sessionService.getGroupSessions(groupId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. Mark a session as completed and generate the AI summary
    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<?> completeSession(@PathVariable Long sessionId, @RequestBody Map<String, String> payload) 
    {
        try {
            // If the user doesn't provide specific notes, we provide a fallback for the AI
            String keyTakeaways = payload.getOrDefault("keyTakeaways", "General group discussion and revision.");
            String aiSummary = sessionService.completeSessionAndGenerateSummary(sessionId, keyTakeaways);
            
            return ResponseEntity.ok(Map.of(
                "message", "Session completed successfully!",
                "aiSummary", aiSummary
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✨ NEW: Endpoint triggered when a user clicks "Join Session"
    @PostMapping("/{sessionId}/attend/{userId}")
    public ResponseEntity<?> markAttendance(@PathVariable Long sessionId, @PathVariable Long userId) {
        try {
            sessionService.recordAttendance(sessionId, userId);
            return ResponseEntity.ok(Map.of("message", "Attendance recorded successfully!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}