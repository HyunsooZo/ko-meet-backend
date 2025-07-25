package com.backend.immilog.global.security;

import com.backend.immilog.global.enums.Country;
import com.backend.immilog.global.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtProvider 테스트")
class JwtProviderTest {
    @Mock
    private UserDetailsServiceImpl userDetailsService;

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        var secretKey = Base64.getEncoder().encodeToString("test-secret-key-for-jwt-testing-purposes-32-chars".getBytes());
        var jwtProperties = new JwtProperties("test-issuer", secretKey, Duration.ofMinutes(30), Duration.ofDays(7));

        jwtProvider = new JwtProvider(jwtProperties, userDetailsService);
        jwtProvider.init();
    }

    @Test
    @DisplayName("토큰 발급 테스트")
    void issueAccessToken_ShouldGenerateValidToken() {
        // given
        var userId = 1L;
        var email = "test@example.com";
        var userRole = UserRole.ROLE_USER;
        var country = Country.SOUTH_KOREA;

        // when
        var token = jwtProvider.issueAccessToken(userId, email, userRole, country);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtProvider.validateToken(token)).isTrue();
        assertThat(jwtProvider.getIdFromToken(token)).isEqualTo(userId);
        assertThat(jwtProvider.getEmailFromToken(token)).isEqualTo(email);
        assertThat(jwtProvider.getUserRoleFromToken(token)).isEqualTo(userRole);
    }

    @Test
    @DisplayName("리프레시 토큰 발급 테스트")
    void issueRefreshToken_ShouldGenerateValidToken() {
        // when
        var refreshToken = jwtProvider.issueRefreshToken();

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(jwtProvider.validateToken(refreshToken)).isTrue();
    }

    @Test
    @DisplayName("토큰 유효성 검사 - 유효하지 않은 토큰")
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // given
        var invalidToken = "invalid.jwt.token";

        // when & then
        assertThat(jwtProvider.validateToken(invalidToken)).isFalse();
    }
}