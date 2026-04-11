package com.Backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /topic  → broadcast to all subscribers (group chat)
        // /queue  → send to a specific user (future use)
        config.enableSimpleBroker("/topic", "/queue");

        // Prefix for messages sent FROM the client TO the server
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint("/chat")          // WebSocket handshake URL
            // ✅ FIX: Use setAllowedOriginPatterns instead of setAllowedOrigins("*")
            // When Spring Security has allowCredentials=true, using "*" causes a
            // CORS rejection during the SockJS handshake. Pattern matching solves this.
            .setAllowedOriginPatterns("*")
            .withSockJS();                 // SockJS fallback for older browsers
    }
}