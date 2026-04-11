package com.Backend.controller;

import com.Backend.model.ChatMessage;
import com.Backend.repository.ChatMessageRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles both:
 *  1. WebSocket STOMP messages  → real-time group broadcasting
 *  2. REST HTTP endpoint        → loading chat history on page open
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    // ✅ Used to PUSH messages to subscribers (the key to real-time chat)
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository repo;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatMessageRepository repo) {
        this.messagingTemplate = messagingTemplate;
        this.repo = repo;
    }

    /**
     * ✅ FIX: WebSocket message handler.
     *
     * Frontend sends a STOMP message to:
     *   /app/chat/{groupId}
     *
     * This method:
     *   1. Sets a timestamp
     *   2. Saves the message to PostgreSQL
     *   3. Broadcasts it to ALL subscribers of /topic/group/{groupId}
     *
     * @param groupId  extracted from the destination path variable
     * @param message  the ChatMessage payload sent by the client (JSON → Java)
     */
    @MessageMapping("/chat/{groupId}")
    public void handleGroupMessage(
            @DestinationVariable Long groupId,
            @Payload ChatMessage message) {

        System.out.println("📨 WebSocket message received for group " + groupId
                + " from " + message.getSender()
                + ": " + message.getContent());

        // Stamp the group ID from the path (ignore whatever client sent)
        message.setGroupId(groupId);
        message.setTimestamp(LocalDateTime.now());

        // Persist to database
        ChatMessage saved = repo.save(message);

        System.out.println("✅ Saved message ID: " + saved.getId()
                + " — broadcasting to /topic/group/" + groupId);

        // ✅ Broadcast to EVERY subscriber of this group's topic
        messagingTemplate.convertAndSend("/topic/group/" + groupId, saved);
    }

    /**
     * REST endpoint — called via Axios when the chat window opens.
     * Returns the full message history for a group from the database.
     *
     * GET /api/chat/{groupId}
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long groupId) {
        List<ChatMessage> messages = repo.findByGroupId(groupId);
        System.out.println("📄 Fetching " + messages.size()
                + " messages for group " + groupId);
        return ResponseEntity.ok(messages);
    }
}