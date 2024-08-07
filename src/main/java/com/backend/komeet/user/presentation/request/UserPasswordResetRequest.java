package com.backend.komeet.user.presentation.request;

import com.backend.komeet.user.enums.Countries;
import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * 사용자 비밀번호 재설정 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "UserPasswordResetRequest", description = "사용자 비밀번호 재설정 요청 DTO")
public class UserPasswordResetRequest {
    private String email;
    private Countries country;
}
