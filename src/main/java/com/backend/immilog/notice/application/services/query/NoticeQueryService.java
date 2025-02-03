package com.backend.immilog.notice.application.services.query;

import com.backend.immilog.global.enums.Country;
import com.backend.immilog.notice.application.result.NoticeResult;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.repositories.NoticeRepository;
import com.backend.immilog.notice.exception.NoticeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.immilog.notice.exception.NoticeErrorCode.NOTICE_NOT_FOUND;

@Service
public class NoticeQueryService {
    private final NoticeRepository noticeRepository;

    public NoticeQueryService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Transactional(readOnly = true)
    public Page<NoticeResult> getNotices(
            Long userSeq,
            Pageable pageable
    ) {
        return noticeRepository.getNotices(userSeq, pageable);
    }

    @Transactional(readOnly = true)
    public Notice getNoticeBySeq(Long noticeSeq) {
        return noticeRepository
                .findBySeq(noticeSeq)
                .orElseThrow(() -> new NoticeException(NOTICE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Boolean areUnreadNoticesExist(
            Country Country,
            Long seq
    ) {
        return noticeRepository.areUnreadNoticesExist(Country, seq);
    }
}
