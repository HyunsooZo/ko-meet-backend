package com.backend.immilog.notice.presentation.response;

import com.backend.immilog.notice.application.result.NoticeResult;
import io.swagger.v3.oas.annotations.media.Schema;

public record NoticeDetailResponse(
        @Schema(description = "상태 코드", example = "200") Integer status,
        @Schema(description = "메시지", example = "success") String message,
        @Schema(description = "공지사항 상세 정보") NoticeResult data
) {
}