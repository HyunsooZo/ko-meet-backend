package com.backend.immilog.user.application;

import com.backend.immilog.global.enums.Country;
import com.backend.immilog.user.application.services.UserReportService;
import com.backend.immilog.user.application.services.command.ReportCommandService;
import com.backend.immilog.user.application.services.command.UserCommandService;
import com.backend.immilog.user.application.services.query.ReportQueryService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.enums.ReportReason;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.user.*;
import com.backend.immilog.user.exception.UserException;
import com.backend.immilog.user.presentation.request.UserReportRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.backend.immilog.global.enums.UserRole.ROLE_USER;
import static com.backend.immilog.user.exception.UserErrorCode.ALREADY_REPORTED;
import static com.backend.immilog.user.exception.UserErrorCode.CANNOT_REPORT_MYSELF;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("사용자 신고 서비스 테스트")
class UserReportDataServiceTest {
    private final UserQueryService userQueryService = mock(UserQueryService.class);
    private final UserCommandService userCommandService = mock(UserCommandService.class);
    private final ReportCommandService reportCommandService = mock(ReportCommandService.class);
    private final ReportQueryService reportQueryService = mock(ReportQueryService.class);
    private final UserReportService userReportService = new UserReportService(
            userQueryService,
            userCommandService,
            reportCommandService,
            reportQueryService
    );

    @Test
    @DisplayName("사용자 신고 성공")
    void reportUser() {
        // given
        Long targetUserSeq = 1L;
        Long reporterUserSeq = 2L;
        //public record User(
        //        Long seq,
        //        Auth auth,
        //        UserRole userRole,
        //        ReportData reportData,
        //        Profile profile,
        //        Location location,
        //        UserStatus userStatus,
        //        LocalDateTime updatedAt
        //) {
        User user = new User(
                targetUserSeq,
                Auth.of("test@emial.com", "test"),
                ROLE_USER,
                ReportData.of(1L, Date.valueOf(LocalDateTime.now().toLocalDate())),
                Profile.of("test", "image", Country.SOUTH_KOREA),
                Location.of(Country.MALAYSIA, "KL"),
                UserStatus.PENDING,
                LocalDateTime.now()
        );
        UserReportRequest reportUserRequest = new UserReportRequest(ReportReason.FRAUD, "test");
        when(reportQueryService.existsByUserSeqNumbers(targetUserSeq, reporterUserSeq)).thenReturn(false);
        when(userQueryService.getUserById(targetUserSeq)).thenReturn(user);
        // when
        userReportService.reportUser(targetUserSeq, reporterUserSeq, reportUserRequest.toCommand());

        // then
        verify(reportCommandService, times(1)).save(any(com.backend.immilog.user.domain.model.report.Report.class));
    }

    @Test
    @DisplayName("사용자 신고 실패: 본인 신고")
    void reportUser_failed_himself() {
        // given
        Long targetUserSeq = 1L;
        Long reporterUserSeq = 1L;
        UserReportRequest reportUserRequest = mock(UserReportRequest.class);
        when(reportQueryService.existsByUserSeqNumbers(targetUserSeq, reporterUserSeq)).thenReturn(false);
        // when & then
        assertThatThrownBy(() -> userReportService.reportUser(
                targetUserSeq,
                reporterUserSeq,
                reportUserRequest.toCommand()
        ))
                .isInstanceOf(UserException.class)
                .hasMessage(CANNOT_REPORT_MYSELF.getMessage());
    }

    @Test
    @DisplayName("사용자 신고 실패: 중복 신고")
    void reportUser_failed_duplicated() {
        // given
        Long targetUserSeq = 1L;
        Long reporterUserSeq = 2L;
        UserReportRequest reportUserRequest =  mock(UserReportRequest.class);
        when(reportQueryService.existsByUserSeqNumbers(targetUserSeq, reporterUserSeq)).thenReturn(true);
        // when & then
        assertThatThrownBy(() -> userReportService.reportUser(
                targetUserSeq,
                reporterUserSeq,
                reportUserRequest.toCommand()
        ))
                .isInstanceOf(UserException.class)
                .hasMessage(ALREADY_REPORTED.getMessage());
    }
}