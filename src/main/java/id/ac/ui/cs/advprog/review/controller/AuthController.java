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
                "user1", UUID.fromString("f0e9d8c7-b6a5-4321-fedc-ba9876543210"),
                "user2", UUID.fromString("11111111-2222-3333-4444-555555555555"),
                "user3", UUID.fromString("99999999-8888-7777-6666-555555555555"),
                "organizer1", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "organizer2", UUID.fromString("bbbbbbbb-cccc-dddd-eeee-ffffffffffff"),
                "admin1", UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef")
        );

        if ("user1".equals(username) && "user1pass".equals(password)) {
            String token = jwtUtil.generateToken(userIds.get("user1").toString(), "User");
            return ResponseEntity.ok(Map.of("token", token));
        } else if ("user2".equals(username) && "user2pass".equals(password)) {
            String token = jwtUtil.generateToken(userIds.get("user2").toString(), "User");
            return ResponseEntity.ok(Map.of("token", token));
        } else if ("user3".equals(username) && "user3pass".equals(password)) {
            String token = jwtUtil.generateToken(userIds.get("user3").toString(), "User");
            return ResponseEntity.ok(Map.of("token", token));
        } else if ("organizer1".equals(username) && "organizer1pass".equals(password)) {
            String token = jwtUtil.generateToken(userIds.get("organizer1").toString(), "Organizer");
            return ResponseEntity.ok(Map.of("token", token));
        } else if ("organizer2".equals(username) && "organizer2pass".equals(password)) {
            String token = jwtUtil.generateToken(userIds.get("organizer2").toString(), "Organizer");
            return ResponseEntity.ok(Map.of("token", token));
        } else if ("admin1".equals(username) && "admin1pass".equals(password)) {
            String token = jwtUtil.generateToken(userIds.get("admin1").toString(), "Admin");
            return ResponseEntity.ok(Map.of("token", token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
    }
}