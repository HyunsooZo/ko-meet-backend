package com.backend.immilog.user.application;

import com.backend.immilog.user.application.services.UserSignUpService;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.exception.UserException;
import com.backend.immilog.user.infrastructure.jpa.repositories.UserJpaRepository;
import com.backend.immilog.user.presentation.request.UserSignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.backend.immilog.user.exception.UserErrorCode.EXISTING_USER;
import static com.backend.immilog.user.exception.UserErrorCode.USER_NOT_FOUND;
import static com.backend.immilog.user.domain.model.enums.UserStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("사용자 회원가입 서비스 테스트")
class UserSignUpServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    private UserSignUpService userSignUpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userSignUpService = new UserSignUpService(
                userRepository,
                passwordEncoder
        );
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signUp_success() {
        // given
        UserSignUpRequest param = UserSignUpRequest.builder()
                .nickName("test")
                .password("test1234")
                .email("email@email.com")
                .country("SOUTH_KOREA")
                .interestCountry("SOUTH_KOREA")
                .region("Seoul")
                .profileImage("image")
                .build();

        User user = User.builder().seq(1L).nickName("test").build();
        when(userRepository.saveEntity(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("test1234");
        Pair<Long, String> seqAndNickName = userSignUpService.signUp(param.toCommand());
        // then
        assertThat(seqAndNickName.getSecond()).isEqualTo("test");
    }

    @Test
    @DisplayName("회원가입 - 실패(이미 존재하는 사용자)")
    void signUp_fail_() {
        UserSignUpRequest param = UserSignUpRequest.builder()
                .nickName("test")
                .password("test1234")
                .email("email@email.com")
                .country("SOUTH_KOREA")
                .interestCountry("SOUTH_KOREA")
                .region("Seoul")
                .profileImage("image")
                .build();

        when(userRepository.getByEmail(anyString()))
                .thenReturn(Optional.of(User.builder().build()));

        assertThatThrownBy(() -> userSignUpService.signUp(param.toCommand()))
                .isInstanceOf(UserException.class)
                .hasMessage(EXISTING_USER.getMessage());
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 중복되지 않은 경우")
    void checkNickname_success() {
        // given
        String nickname = "test";
        when(userRepository.getByUserNickname(nickname)).thenReturn(Optional.empty());
        // when
        Boolean result = userSignUpService.checkNickname(nickname);
        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 중복된 경우")
    void checkNickname_fail() {
        // given
        String nickname = "test";
        when(userRepository.getByUserNickname(nickname)).thenReturn(Optional.of(User.builder().build()));
        // when
        Boolean result = userSignUpService.checkNickname(nickname);
        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("이메일 인증 - 성공")
    void verifyEmail_success() {
        // given
        Long userSeq = 1L;
        User user = User.builder().userStatus(PENDING).build();
        when(userRepository.getById(userSeq)).thenReturn(Optional.of(user));
        // when
        Pair<String, Boolean> result = userSignUpService.verifyEmail(userSeq);
        // then
        assertThat(result.getSecond()).isTrue();
    }

    @Test
    @DisplayName("이메일 인증 - 실패(사용자 없음)")
    void verifyEmail_fail_userNotFound() {
        // given
        Long userSeq = 1L;
        when(userRepository.getById(userSeq)).thenReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> userSignUpService.verifyEmail(userSeq))
                .isInstanceOf(UserException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("이메일 인증 - 실패(이미 인증된 사용자)")
    void verifyEmail_fail_already_verified() {
        // given
        Long userSeq = 1L;
        User user = User.builder().userStatus(ACTIVE).build();
        when(userRepository.getById(userSeq)).thenReturn(Optional.of(user));
        // when
        Pair<String, Boolean> result = userSignUpService.verifyEmail(userSeq);
        // then
        assertThat(result.getFirst()).isEqualTo("이미 인증된 사용자입니다.");
        assertThat(result.getSecond()).isTrue();
    }

    @Test
    @DisplayName("이메일 인증 - 실패(차단된 사용자)")
    void verifyEmail_fail_blocked() {
        // given
        Long userSeq = 1L;
        User user = User.builder().userStatus(BLOCKED).build();
        when(userRepository.getById(userSeq)).thenReturn(Optional.of(user));
        // when
        Pair<String, Boolean> result = userSignUpService.verifyEmail(userSeq);
        // then
        assertThat(result.getFirst()).isEqualTo("차단된 사용자입니다.");
        assertThat(result.getSecond()).isFalse();
    }

    @Test
    @DisplayName("이메일 인증 - 실패(차단된 사용자)")
    void verifyEmail_fail_other_cases() {
        // given
        Long userSeq = 1L;
        User user = User.builder().userStatus(REPORTED).build();
        when(userRepository.getById(userSeq)).thenReturn(Optional.of(user));
        // when
        Pair<String, Boolean> result = userSignUpService.verifyEmail(userSeq);
        // then
        assertThat(result.getFirst()).isEqualTo("이메일 인증이 필요한 사용자가 아닙니다.");
        assertThat(result.getSecond()).isTrue();
    }

}