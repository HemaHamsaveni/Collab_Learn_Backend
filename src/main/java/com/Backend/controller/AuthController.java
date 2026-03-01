package com.Backend.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin; // NEW IMPORT
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; // NEW IMPORT
import org.springframework.web.bind.annotation.RestController;

import com.Backend.dto.AuthResponse;
import com.Backend.dto.LoginRequest;
import com.Backend.dto.RegisterRequest;
import com.Backend.model.User;
import com.Backend.service.UserService;
import com.Backend.util.JwtUtil;



@RestController

@RequestMapping("/api/auth")

@CrossOrigin(origins = "http://localhost:5173")

public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired

    private JwtUtil jwtUtil; // INJECT THE WRISTBAND MAKER



    @PostMapping("/register")

    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        try {

            // 1. Register the user just like before

            User user = userService.registerUser(request);

           

            // 2. FIXED: Use 'jwtUtil' and pass the email!

            String token = jwtUtil.generateToken(user.getEmail());

           

            // 3. FIXED: Use your existing AuthResponse (just like your login method)

            return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getName()));

           

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }



    @PostMapping("/login")

    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {

            // 1. Verify password (throws error if wrong)

            User user = userService.loginUser(request);

           

            // 2. Generate the Token!

            String token = jwtUtil.generateToken(user.getEmail());

           

            // 3. Send back the VIP Wristband + User Info

            return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getName()));

           

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }

}