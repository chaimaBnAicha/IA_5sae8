package com.example.backend.config;

import com.example.backend.Utils.CustomUserDetails;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret; // Base64 encoded secret key

    @Value("${jwt.expiration}")
    private long validityInMilliseconds; // Expiration time (in milliseconds)

    private SecretKey secretKey;

    // Initialize the secret key after the properties are injected
    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // Generate a JWT token based on the authentication details (username, roles, etc.)
    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal(); // ✅
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Set username as the subject
                .setIssuedAt(now)
                .setExpiration(validity) // Set the expiration time
                .signWith(secretKey, SignatureAlgorithm.HS256) // Sign the JWT with the secret key
                .compact();
    }

    // Validate the JWT token (check signature, expiration, etc.)
    public boolean validateToken(String token) {
        try {
            System.out.println("Validating token with secret: " + new String(Base64.getEncoder().encode(secretKey.getEncoded())));
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            System.out.println("Token validated successfully for user: " + claims.getBody().getSubject());
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid token: " + e.getMessage());
            e.printStackTrace(); // Add this to see full stack trace
        }
        return false;
    }

    // Extract the username (subject) from the JWT token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract the Authentication object from the JWT token
    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        User user = new User(username, "", new ArrayList<>()); // Empty authorities (you can add them if needed)
        return new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
    }

    // Check if the token is expired
    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = Jwts.parserBuilder().setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expirationDate.before(new Date());
        } catch (JwtException e) {
            return true; // Token is invalid or expired
        }
    }

    // Extract the token from the HTTP request
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
