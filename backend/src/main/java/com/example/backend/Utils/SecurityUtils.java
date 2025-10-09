package com.example.backend.Utils;

import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtils {
    public static void main(String[] args) {
        // Générer une clé secrète JWT de 256 bits (32 bytes)
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        String secretKey = Base64.getEncoder().encodeToString(key);
        System.out.println("JWT Secret Key: " + secretKey);
    }
}