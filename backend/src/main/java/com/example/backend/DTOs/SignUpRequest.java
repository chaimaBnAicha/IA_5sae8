package com.example.backend.DTOs;

import com.example.backend.entities.Role;
import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Role role;
}