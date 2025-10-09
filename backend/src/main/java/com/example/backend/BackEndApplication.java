package com.example.backend;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Base64;

@EnableDiscoveryClient
@SpringBootApplication
public class BackEndApplication {

	static {
		System.setProperty("mail.smtp.ssl.trust", "*");
		System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
	}

	public static void main(String[] args) {
		// Generate and print the JWT secret key for HS256
		byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
		String encodedKey = Base64.getEncoder().encodeToString(key);
		System.out.println("Generated JWT Secret Key (Base64 encoded): " + encodedKey);

		// Start the Spring Boot application
		SpringApplication.run(BackEndApplication.class, args);
	}
}
