package com.backend.immilog.notice.application.services;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.global.security.TokenProvider;
import com.backend.immilog.notice.application.command.NoticeModifyCommand;
import com.backend.immilog.notice.application.services.command.NoticeCommandService;
import com.backend.immilog.notice.application.services.query.NoticeQueryService;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.exception.NoticeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.backend.immilog.notice.domain.model.enums.NoticeStatus.DELETED;
import static com.backend.immilog.notice.exception.NoticeErrorCode.NOTICE_NOT_FOUND;
import static com.backend.immilog.notice.exception.NoticeErrorCode.NOT_AN_ADMIN_USER;

@Service
@RequiredArgsConstructor
public class NoticeModifyService {
    private final NoticeQueryService noticeQueryService;
    private final NoticeCommandService noticeCommandService;
    private final TokenProvider tokenProvider;

    @Transactional
    public void modifyNotice(
            String token,
            Long noticeSeq,
            NoticeModifyCommand command
    ) {
        validateAdmin(token);
        Notice notice = getNoticeBySeq(noticeSeq);
        notice.updateTitle(command.title());
        notice.updateContent(command.content());
        notice.updateType(command.type());
        notice.updateStatus(command.status());
        noticeCommandService.save(notice);
    }

    private void validateAdmin(String token) {
        Optional.ofNullable(tokenProvider.getUserRoleFromToken(token))
                .filter(role -> role.equals(UserRole.ROLE_ADMIN))
                .orElseThrow(() -> new NoticeException(NOT_AN_ADMIN_USER));
    }

    private Notice getNoticeBySeq(
            Long noticeSeq
    ) {
        return noticeQueryService
                .getNoticeBySeq(noticeSeq)
                .filter(notice -> notice.getStatus() != DELETED)
                .orElseThrow(() -> new NoticeException(NOTICE_NOT_FOUND));
    }

    public void readNotice(
            Long userSeq,
            Long noticeSeq
    ) {
        Notice notice = getNoticeBySeq(noticeSeq);
        notice.readByUser(userSeq);
        noticeCommandService.save(notice);
    }
}

