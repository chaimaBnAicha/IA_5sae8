package com.example.backend.DTOs;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}