package id.ac.ui.cs.advprog.review.controller;

import id.ac.ui.cs.advprog.review.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Map<String, UUID> userIds = Map.of(
                "user", UUID.fromString("f0e9d8c7-b6a5-4321-fedc-ba9876543210"),
                "admin", UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef"),
                "organizer", UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        );

        if ("user".equals(username) && "userpass".equals(password)) {
            String token = jwtUtil.generateToken(userIds.get("user").toString(), "User");
            return ResponseEntity.ok(Map.of("token", token));
        } else if ("admin".equals(username) && "adminpass".equals(password)) {
            String token = jwtUtil.generateToken(userIds.get("admin").toString(), "Admin");
            return ResponseEntity.ok(Map.of("token", token));
        } else if ("organizer".equals(username) && "organizerpass".equals(password)) {
            String token = jwtUtil.generateToken(userIds.get("organizer").toString(), "Organizer");
            return ResponseEntity.ok(Map.of("token", token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
    }
}