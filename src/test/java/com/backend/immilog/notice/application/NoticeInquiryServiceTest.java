package com.backend.immilog.notice.application;

import com.backend.immilog.global.enums.Country;
import com.backend.immilog.notice.application.dto.NoticeModelResult;
import com.backend.immilog.notice.application.services.NoticeQueryService;
import com.backend.immilog.notice.application.usecase.NoticeFetchUseCase;
import com.backend.immilog.notice.domain.Notice;
import com.backend.immilog.notice.domain.NoticeDetail;
import com.backend.immilog.notice.domain.NoticeStatus;
import com.backend.immilog.notice.domain.NoticeType;
import com.backend.immilog.user.application.services.UserQueryService;
import com.backend.immilog.user.domain.model.user.Location;
import com.backend.immilog.user.domain.model.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("공지사항 조회 테스트")
class NoticeInquiryServiceTest {
    private final NoticeQueryService noticeQueryService = mock(NoticeQueryService.class);
    private final UserQueryService userQueryService = mock(UserQueryService.class);
    private final NoticeFetchUseCase noticeInquiryService = new NoticeFetchUseCase.NoticeFetcher(noticeQueryService, userQueryService);

    @Test
    @DisplayName("공지사항 조회 - 성공")
    void getNotices_Success() {
        // given
        Long userSeq = 1L;
        int page = 0;
        Notice notice = new Notice(
                1L,
                1L,
                List.of(Country.SOUTH_KOREA),
                List.of(1L),
                LocalDateTime.now(),
                NoticeDetail.of("title", "content", NoticeType.NOTICE, NoticeStatus.NORMAL),
                LocalDateTime.now()
        );

        when(noticeQueryService.getNotices(userSeq, PageRequest.of(page, 10)))
                .thenReturn(new PageImpl<>(List.of(NoticeModelResult.from(notice))));
        // when
        Page<NoticeModelResult> notices = noticeInquiryService.getNotices(userSeq, page);

        // then
        assertThat(notices.get().findFirst().get().content()).isEqualTo("content");
    }

    @Test
    @DisplayName("공지사항 조회 - 실패")
    void getNotices_Fail() {
        // given
        Long userSeq = null;
        int page = 0;

        // when
        Page<NoticeModelResult> notices = noticeInquiryService.getNotices(userSeq, page);

        // then
        assertThat(notices).isEmpty();
    }

    @Test
    @DisplayName("공지사항 상세 조회 - 성공")
    void getNoticeDetail_Success() {
        // given
        Long noticeSeq = 1L;
        Notice notice = new Notice(
                1L,
                1L,
                List.of(Country.SOUTH_KOREA),
                List.of(1L),
                LocalDateTime.now(),
                NoticeDetail.of("title", "content", NoticeType.NOTICE, NoticeStatus.NORMAL),
                LocalDateTime.now()
        );

        when(noticeQueryService.getNoticeBySeq(noticeSeq)).thenReturn(notice);
        // when
        NoticeModelResult noticeDTO = noticeInquiryService.getNoticeDetail(noticeSeq);

        // then
        assertThat(noticeDTO.content()).isEqualTo("content");
    }

    @Test
    @DisplayName("안읽은 공지사항 여부 체크")
    void areUnreadNoticesExist() {
        // given
        Long userSeq = 1L;
        User user = new User(
                1L,
                null,
                null,
                null,
                null,
                Location.of(Country.SOUTH_KOREA, "서울"),
                null,
                null
        );

        when(noticeQueryService.areUnreadNoticesExist(Country.SOUTH_KOREA, userSeq)).thenReturn(true);
        when(userQueryService.getUserById(userSeq)).thenReturn(user);

        // when
        boolean result = noticeInquiryService.isUnreadNoticeExist(userSeq);

        // then
        assertThat(result).isTrue();
    }
}