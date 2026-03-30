package com.Backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.Backend.model.StudyGroup;
import com.Backend.model.StudySession;
import com.Backend.model.User;
import com.Backend.model.enums.SessionStatus;
import com.Backend.repository.StudyGroupRepository;
import com.Backend.repository.StudySessionRepository;
import com.Backend.repository.UserRepository;

@Service
public class StudySessionService {

    @Autowired
    private StudySessionRepository sessionRepository;

    @Autowired
    private StudyGroupRepository groupRepository;

    @Autowired
    private GeminiService geminiService;

    // 1. Create a new session
    public StudySession createSession(Long groupId, StudySession sessionRequest) {
        StudyGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        sessionRequest.setStudyGroup(group);
        sessionRequest.setStatus(SessionStatus.UPCOMING);
        return sessionRepository.save(sessionRequest);
    }

    // 2. Fetch all sessions for a group
    public List<StudySession> getGroupSessions(Long groupId) {
        return sessionRepository.findByStudyGroupId(groupId);
    }

   // 3. Mark as Complete & Generate AI Summary
    public String completeSessionAndGenerateSummary(Long sessionId, String keyTakeaways) {
        StudySession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        // 1. Tell Gemini to generate the summary FIRST
        String prompt = String.format(
            "You are an AI tutor. A study group just finished a session on '%s'. " +
            "Here are their rough notes/takeaways: %s. " +
            "Please write a clean, formatted 3-bullet-point summary of what they learned.",
            session.getTopic(), keyTakeaways
        );
        String generatedSummary = geminiService.generateContent(prompt);

        // 2. Update the status AND save the summary to the database
        session.setStatus(SessionStatus.COMPLETED);
        session.setAiSummary(generatedSummary); // ✨ SAVING THE SUMMARY HERE
        sessionRepository.save(session);

        return generatedSummary;
    }

    // ✨ 4. THE AUTOMATION: Runs every hour to clean up old sessions
    @Scheduled(cron = "0 0 * * * *") // Runs at the top of every hour
    public void autoUpdateMissedSessions() {
        System.out.println("Running background check for expired sessions...");
        List<StudySession> allSessions = sessionRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (StudySession session : allSessions) {
            if (session.getStatus() == SessionStatus.UPCOMING && session.getScheduledTime().isBefore(now)) {
                // If the time has passed and nobody marked it complete, it was missed
                session.setStatus(SessionStatus.CANCELLED); // Or create a MISSED enum
                sessionRepository.save(session);
                System.out.println("Marked session " + session.getId() + " as missed.");
            }
        }
    }

    @Autowired
    private UserRepository userRepository; // Ensure this is autowired at the top!

    // ✨ NEW: Record that a user joined the meeting
    public void recordAttendance(Long sessionId, Long userId) {
        StudySession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        session.getAttendees().add(user);
        sessionRepository.save(session);
    }
}