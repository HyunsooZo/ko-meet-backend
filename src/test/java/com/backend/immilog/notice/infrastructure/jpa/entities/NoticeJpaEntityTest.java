package com.backend.immilog.notice.infrastructure.jpa.entities;

import com.backend.immilog.global.enums.Country;
import com.backend.immilog.notice.domain.Notice;
import com.backend.immilog.notice.domain.NoticeDetail;
import com.backend.immilog.notice.domain.NoticeStatus;
import com.backend.immilog.notice.domain.NoticeType;
import com.backend.immilog.notice.infrastructure.jpa.NoticeJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NoticeEntity 테스트")
class NoticeJpaEntityTest {

    @Test
    @DisplayName("NoticeEntity 생성 테스트")
    void from_createsNoticeEntityFromNotice() {
        Notice notice = new Notice(
                1L,
                2L,
                List.of(Country.SINGAPORE),
                List.of(3L),
                null,
                NoticeDetail.of("Title", "Content", NoticeType.NOTICE, NoticeStatus.NORMAL),
                null
        );
        NoticeJpaEntity noticeJpaEntity = NoticeJpaEntity.from(notice);
        Notice domain = noticeJpaEntity.toDomain();

        assertThat(domain.seq()).isEqualTo(notice.seq());
        assertThat(domain.userSeq()).isEqualTo(notice.userSeq());
        assertThat(domain.title()).isEqualTo(notice.title());
        assertThat(domain.content()).isEqualTo(notice.content());
        assertThat(domain.type()).isEqualTo(notice.type());
        assertThat(domain.status()).isEqualTo(notice.status());
        assertThat(domain.targetCountry()).isEqualTo(notice.targetCountry());
        assertThat(domain.readUsers()).isEqualTo(notice.readUsers());
    }

    @Test
    @DisplayName("NoticeEntity toDomain 메서드 테스트")
    void toDomain_createsNoticeFromNoticeEntity() {

        NoticeJpaEntity noticeJpaEntity = NoticeJpaEntity.from(new Notice(
                1L,
                2L,
                List.of(Country.SINGAPORE),
                List.of(3L),
                null,
                NoticeDetail.of("Title", "Content", NoticeType.NOTICE, NoticeStatus.NORMAL),
                null
        ));

        Notice notice = noticeJpaEntity.toDomain();

        assertThat(notice.seq()).isEqualTo(1L);
        assertThat(notice.userSeq()).isEqualTo(2L);
        assertThat(notice.title()).isEqualTo("Title");
        assertThat(notice.content()).isEqualTo("Content");
        assertThat(notice.type()).isEqualTo(NoticeType.NOTICE);
        assertThat(notice.status()).isEqualTo(NoticeStatus.NORMAL);
        assertThat(notice.targetCountry()).isEqualTo(List.of(Country.SINGAPORE));
        assertThat(notice.readUsers()).isEqualTo(List.of(3L));
    }

    @Test
    @DisplayName("NoticeEntity null 값 처리 테스트")
    void from_handlesNullValues() {
        Notice notice = new Notice(
                null,
                null,
                null,
                null,
                null,
                NoticeDetail.of(null, null, null, null),
                null
        );

        NoticeJpaEntity noticeJpaEntity = NoticeJpaEntity.from(notice);
    }

    @Test
    @DisplayName("NoticeEntity toDomain null 값 처리 테스트")
    void toDomain_handlesNullValues() {
        NoticeJpaEntity noticeJpaEntity = NoticeJpaEntity.from(
                new Notice(
                        null,
                        null,
                        null,
                        null,
                        null,
                        NoticeDetail.of(null, null, null, null),
                        null
                )
        );
        Notice notice = noticeJpaEntity.toDomain();
        assertThat(notice.seq()).isNull();
        assertThat(notice.userSeq()).isNull();
        assertThat(notice.title()).isNull();
        assertThat(notice.content()).isNull();
        assertThat(notice.type()).isNull();
        assertThat(notice.status()).isNull();
        assertThat(notice.targetCountry()).isNull();
        assertThat(notice.readUsers()).isNull();
    }
}