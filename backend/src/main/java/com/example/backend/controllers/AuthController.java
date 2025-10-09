package com.example.backend.controllers;

import com.example.backend.DTOs.ErrorResponse;
import com.example.backend.DTOs.LoginRequest;
import com.example.backend.DTOs.SignUpRequest;
import com.example.backend.Utils.CustomUserDetails;
import com.example.backend.config.JwtTokenProvider;
import com.example.backend.entities.LoginResponse;
import com.example.backend.entities.User;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired  // Add this annotation
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;
    // ... rest of the code


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        try {
            // Log pour déboguer
            System.out.println("Received signup request for user: " + signUpRequest.getUsername());
            
            // Validation basique
            if (signUpRequest.getUsername() == null || signUpRequest.getEmail() == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Username and email are required"));
            }

            User user = userService.registerUser(signUpRequest);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Received login request - username: " + loginRequest.getUsername());
            System.out.println("Received login request - password length: " +
                    (loginRequest.getPassword() != null ? loginRequest.getPassword().length() : "null"));

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            System.out.println("Authentication successful for user: " + loginRequest.getUsername());

            // Generate token
            String token = jwtTokenProvider.generateToken(authentication);
            System.out.println("Generated token: " + token);

            // Get user details from authentication
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal(); // ✅

            // Prepare the login response
            LoginResponse loginResponse = new LoginResponse(userDetails.getUser(), token);
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            System.err.println("Login failed for user " + loginRequest.getUsername() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid username or password"));
        }
    }

   /* @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }

            System.out.println("Processing password reset request for email: " + email);
            String resetToken = String.valueOf(userService.requestPasswordReset(email)); // Changed to receive String
            return ResponseEntity.ok().body(Map.of(
                    "message", "Password reset email sent",
                    "token", resetToken
            ));
        } catch (Exception e) {
            System.err.println("Error in forgot password: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }*/
       @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }

            System.out.println("Processing password reset request for email: " + email);
            String resetToken = String.valueOf(userService.requestPasswordReset(email));
            
            if (resetToken != null) {
                String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
                return ResponseEntity.ok().body(Map.of(
                        "message", "Password reset email sent successfully",
                        "token", resetToken,
                        "resetLink", resetLink
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Email not found"));
            }
        } catch (Exception e) {
            System.err.println("Error in forgot password: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

       @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String newPassword = request.get("newPassword");
            
            System.out.println("Reset password request for email: " + email); // Debug log
            
            if (email == null || newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email and new password are required"));
            }

            // Mise à jour du mot de passe
            boolean updated = userService.updateUserPassword(email, newPassword);
            
            if (updated) {
                return ResponseEntity.ok().body(Map.of("message", "Password successfully updated"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Failed to update password"));
            }
        } catch (Exception e) {
            System.err.println("Error in reset password: " + e.getMessage()); // Debug log
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            userService.verifyUser(token);
            return ResponseEntity.ok().body(Map.of("message", "Email verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}