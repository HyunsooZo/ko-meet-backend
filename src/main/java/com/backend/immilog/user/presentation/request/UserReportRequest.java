package com.backend.immilog.user.presentation.request;

import com.backend.immilog.user.application.dto.UserReportCommand;
import com.backend.immilog.user.enums.ReportReason;
import io.swagger.annotations.ApiModel;
import lombok.Builder;

@Builder
@ApiModel(value = "UserReportRequest", description = "사용자 신고 요청 DTO")
public record UserReportRequest(
        ReportReason reason,
        String description
) {
    public UserReportCommand toCommand() {
        return UserReportCommand.builder()
                .reason(reason)
                .description(description)
                .build();
    }
}
