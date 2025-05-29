package id.ac.ui.cs.advprog.review.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private final String testSecret = "testSecretKeyForAdvprogReviewApplicationWhichIsVerySecure1234567890";
    private Key signingKey;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        byte[] keyBytes = testSecret.getBytes(StandardCharsets.UTF_8);
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(String userId, String role, long expirationTimeMillis) {
        Map<String, Object> claims = new HashMap<>();
        if (role != null) {
            claims.put("role", role);
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }
    private String generateTokenWithAlternateRoleClaim(String userId, String role, long expirationTimeMillis) {
        Map<String, Object> claims = new HashMap<>();
        if (role != null) {
            claims.put("ROLE", role);
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    @Test
    void testGetUserIdFromJWT() {
        String userId = UUID.randomUUID().toString();
        String token = generateToken(userId, "User", 1000 * 60 * 60);
        assertEquals(userId, jwtTokenProvider.getUserIdFromJWT(token));
    }

    @Test
    void testGetRoleFromJWT_withRoleClaim() {
        String userId = UUID.randomUUID().toString();
        String role = "Admin";
        String token = generateToken(userId, role, 1000 * 60 * 60);
        assertEquals(role, jwtTokenProvider.getRoleFromJWT(token));
    }

    @Test
    void testGetRoleFromJWT_withAlternateROLEClaim() {
        String userId = UUID.randomUUID().toString();
        String role = "Organizer";
        String token = generateTokenWithAlternateRoleClaim(userId, role, 1000 * 60 * 60);
        assertEquals(role, jwtTokenProvider.getRoleFromJWT(token));
    }

    @Test
    void testGetRoleFromJWT_noRoleClaim() {
        String userId = UUID.randomUUID().toString();
        String token = generateToken(userId, null, 1000 * 60 * 60);
        assertNull(jwtTokenProvider.getRoleFromJWT(token));
    }

    @Test
    void testValidateToken_validToken() {
        String userId = UUID.randomUUID().toString();
        String token = generateToken(userId, "User", 1000 * 60 * 60);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void testValidateToken_expiredToken() {
        String userId = UUID.randomUUID().toString();
        String token = generateToken(userId, "User", -1000);
        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    void testValidateToken_invalidSignature() {

        Key otherSigningKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        String token = Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(otherSigningKey, SignatureAlgorithm.HS512)
                .compact();

        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    void testValidateToken_malformedToken() {
        String malformedToken = "this.is.not.a.jwt";
        assertFalse(jwtTokenProvider.validateToken(malformedToken));
    }

    @Test
    void testGetClaimsDirectlyForCoverage_validToken() {
        String userId = UUID.randomUUID().toString();
        String role = "TestRole";
        String token = generateToken(userId, role, 1000 * 60 * 60);

        Claims claims = ReflectionTestUtils.invokeMethod(jwtTokenProvider, "getClaims", token);
        assertNotNull(claims);
        assertEquals(userId, claims.getSubject());
        assertEquals(role, claims.get("role", String.class));
    }
}