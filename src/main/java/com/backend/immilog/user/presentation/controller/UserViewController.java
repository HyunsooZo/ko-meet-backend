package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.user.application.usecase.SignUpUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User View API", description = "사용자 뷰 관련 API")
@RequestMapping("/api/v1/users")
@Controller
public class UserViewController {
    private final SignUpUserUseCase.UserSignUpProcessor userSignUpProcessor;

    public UserViewController(SignUpUserUseCase.UserSignUpProcessor userSignUpProcessor) {
        this.userSignUpProcessor = userSignUpProcessor;
    }

    @GetMapping("/{userId}/verification")
    @Operation(summary = "사용자 이메일 인증", description = "사용자 이메일 인증 진행")
    public String verifyEmail(
            @Parameter(description = "사용자 고유번호") @PathVariable("userId") String userId,
            Model model
    ) {
        final var result = userSignUpProcessor.verifyEmail(userId);
        model.addAttribute("message", result.message());
        model.addAttribute("isLoginAvailable", result.isLoginAvailable());
        return "verification-result";
    }
}
