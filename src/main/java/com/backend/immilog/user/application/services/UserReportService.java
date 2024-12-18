package com.backend.immilog.user.application.services;

import com.backend.immilog.global.infrastructure.persistence.lock.RedisDistributedLock;
import com.backend.immilog.user.application.command.UserReportCommand;
import com.backend.immilog.user.application.services.command.ReportCommandService;
import com.backend.immilog.user.application.services.command.UserCommandService;
import com.backend.immilog.user.application.services.query.ReportQueryService;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.model.report.Report;
import com.backend.immilog.user.domain.model.user.User;
import com.backend.immilog.user.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.backend.immilog.user.domain.enums.ReportReason.OTHER;
import static com.backend.immilog.user.exception.UserErrorCode.*;

@Slf4j
@Service
public class UserReportService {
    final String LOCK_KEY = "reportUser : ";
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final ReportCommandService reportCommandService;
    private final ReportQueryService reportQueryService;
    private final RedisDistributedLock redisDistributedLock;

    public UserReportService(
            UserQueryService userQueryService,
            UserCommandService userCommandService,
            ReportCommandService reportCommandService,
            ReportQueryService reportQueryService,
            RedisDistributedLock redisDistributedLock
    ) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
        this.reportCommandService = reportCommandService;
        this.reportQueryService = reportQueryService;
        this.redisDistributedLock = redisDistributedLock;
    }

    private static Report createReport(
            Long targetUserSeq,
            Long reporterUserSeq,
            UserReportCommand userReportCommand
    ) {
        return Report.of(
                targetUserSeq,
                reporterUserSeq,
                userReportCommand,
                userReportCommand.reason().equals(OTHER)
        );
    }

    @Async
    public void reportUser(
            Long targetUserSeq,
            Long reporterUserSeq,
            UserReportCommand userReportCommand
    ) {
        reportValidation(targetUserSeq, reporterUserSeq);
        boolean lockAcquired = false;
        try {
            lockAcquired = redisDistributedLock.tryAcquireLock(LOCK_KEY, targetUserSeq.toString());
            if (lockAcquired) {
                processReport(targetUserSeq, reporterUserSeq, userReportCommand);
            } else {
                log.error(
                        "사용자 신고 실패 - 원인: 락 획득 실패  targetUserSeq: {}, time: {}",
                        targetUserSeq,
                        LocalDateTime.now()
                );
            }
        } finally {
            if (lockAcquired) {
                redisDistributedLock.releaseLock(LOCK_KEY, targetUserSeq.toString());
            }
        }
    }

    private void processReport(
            Long targetUserSeq,
            Long reporterUserSeq,
            UserReportCommand userReportCommand
    ) {
        User user = getUser(targetUserSeq);
        user.increaseReportedCount();
        Report report = createReport(targetUserSeq, reporterUserSeq, userReportCommand);
        reportCommandService.save(report);
        userCommandService.save(user);
        log.info("User {} reported by {}", targetUserSeq, reporterUserSeq);
    }

    private void reportValidation(
            Long targetUserSeq,
            Long reporterUserSeq
    ) {
        validateDifferentUsers(targetUserSeq, reporterUserSeq);
        validateItsNotDuplicatedReport(targetUserSeq, reporterUserSeq);
    }

    private void validateItsNotDuplicatedReport(
            Long targetUserSeq,
            Long reporterUserSeq
    ) {
        if (targetUserSeq.equals(reporterUserSeq)) {
            throw new UserException(CANNOT_REPORT_MYSELF);
        }
    }

    private void validateDifferentUsers(
            Long targetUserSeq,
            Long reporterUserSeq
    ) {
        boolean isExist = reportQueryService.existsByUserSeqNumbers(
                targetUserSeq,
                reporterUserSeq
        );
        if (isExist) {
            throw new UserException(ALREADY_REPORTED);
        }
    }

    private User getUser(
            Long targetUserSeq
    ) {
        return userQueryService
                .getUserById(targetUserSeq)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
}
