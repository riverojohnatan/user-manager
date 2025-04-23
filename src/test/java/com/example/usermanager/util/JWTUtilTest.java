package com.example.usermanager.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JWTUtilTest {
    
    private final JWTUtil jwtUtil = new JWTUtil();
    private final String SECRET_KEY = "test-secret-key";

    @Test
    void shouldGenerateValidToken() {
        ReflectionTestUtils.setField(jwtUtil, "JWT_SECRET", SECRET_KEY);

        String email = "user@example.com";

        // Act
        String token = jwtUtil.generateToken(email);

        // Assert
        assertThat(token).isNotBlank();
        String subject = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        assertThat(subject).isEqualTo(email);
    }

    @Test
    void shouldContainIssuedAtAndExpirationDates() {
        ReflectionTestUtils.setField(jwtUtil, "JWT_SECRET", SECRET_KEY);

        String email = "user@example.com";

        // Act
        String token = jwtUtil.generateToken(email);

        // Assert
        Date issuedAt = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getIssuedAt();
        Date expiration = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        assertThat(issuedAt).isNotNull();
        assertThat(expiration).isNotNull();
        assertThat(expiration.getTime()).isGreaterThan(issuedAt.getTime());
        assertThat(expiration.getTime() - issuedAt.getTime()).isEqualTo(86400000);
    }

    @Test
    void shouldThrowExceptionForInvalidTokenParsing() {
        ReflectionTestUtils.setField(jwtUtil, "JWT_SECRET", SECRET_KEY);

        String invalidSECRET_KEY = "invalid-secret-key";
        String email = "user@example.com";
        String token = jwtUtil.generateToken(email);

        // Act & Assert
        Throwable thrown = org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            Jwts.parser()
                    .setSigningKey(invalidSECRET_KEY)
                    .parseClaimsJws(token);
        });

        assertThat(thrown).isNotNull();
    }
}