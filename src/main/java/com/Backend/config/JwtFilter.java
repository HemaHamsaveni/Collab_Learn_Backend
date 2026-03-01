package com.Backend.config;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Backend.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        System.out.println("\n--- NEW REQUEST TO: " + request.getMethod() + " " + request.getRequestURI() + " ---");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("-> Passing OPTIONS request automatically.");
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("-> Auth Header: " + (authorizationHeader != null ? "Found!" : "NULL"));

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(jwt);
                System.out.println("-> Extracted Email: " + email);
            } catch (Exception e) {
                System.out.println("-> ❌ JWT Extraction Error: " + e.getMessage());
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            boolean isValid = jwtUtil.validateToken(jwt);
            System.out.println("-> Is Token Valid? " + isValid);
            
            if (isValid) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("-> ✅ User Authenticated successfully!");
            } else {
                System.out.println("-> ❌ Token validation returned FALSE!");
            }
        } else {
            System.out.println("-> ⚠️ Email was null OR user is already authenticated.");
        }
        
        chain.doFilter(request, response);
        System.out.println("--- REQUEST FINISHED ---\n");
    }
}