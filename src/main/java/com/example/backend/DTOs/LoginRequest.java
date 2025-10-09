package com.example.backend.DTOs;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}