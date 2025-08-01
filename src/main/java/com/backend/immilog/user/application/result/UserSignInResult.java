package com.backend.immilog.user.application.result;

import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.presentation.payload.UserSignInPayload;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserSignInResult(
        @Schema(description = "사용자 식별자", example = "1") String userId,
        @Schema(description = "이메일", example = "email@email.com") String email,
        @Schema(description = "닉네임", example = "nickname") String nickname,
        @Schema(description = "액세스 토큰", example = "access token") String accessToken,
        @Schema(description = "리프레시 토큰", example = "refresh token") String refreshToken,
        @Schema(description = "국가", example = "Korea") String country,
        @Schema(description = "관심 국가", example = "Korea") String interestCountry,
        @Schema(description = "지역", example = "Seoul") String region,
        @Schema(description = "프로필 이미지 URL", example = "profile image url") String userProfileUrl,
        @Schema(description = "위치 일치 여부", example = "true") Boolean isLocationMatch
) {
    public static UserSignInResult of(
            User user,
            String accessToken,
            String refreshToken,
            boolean isLocationMatch
    ) {
        return new UserSignInResult(
                user.getUserId().value(),
                user.getEmail(),
                user.getNickname(),
                accessToken == null ? "" : accessToken,
                refreshToken == null ? "" : refreshToken,
                user.getCountry().koreanName(),
                user.getInterestCountry() == null ? null : user.getCountry().koreanName(),
                user.getRegion(),
                user.getImageUrl(),
                isLocationMatch
        );
    }

    public UserSignInPayload.UserSignInResponse toResponse() {
        return new UserSignInPayload.UserSignInResponse(200, "success", this);
    }
}

