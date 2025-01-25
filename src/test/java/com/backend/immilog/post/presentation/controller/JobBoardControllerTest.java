package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.post.application.result.JobBoardResult;
import com.backend.immilog.post.application.services.JobBoardInquiryService;
import com.backend.immilog.post.application.services.JobBoardUpdateService;
import com.backend.immilog.post.application.services.JobBoardUploadService;
import com.backend.immilog.post.domain.enums.Countries;
import com.backend.immilog.post.domain.enums.Experience;
import com.backend.immilog.post.domain.enums.PostStatus;
import com.backend.immilog.post.presentation.request.JobBoardUpdateRequest;
import com.backend.immilog.post.presentation.request.JobBoardUploadRequest;
import com.backend.immilog.post.presentation.response.PostApiResponse;
import com.backend.immilog.user.domain.enums.Industry;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("JobBoardController 테스트")
class JobBoardControllerTest {
    private final JobBoardUploadService jobBoardUploadService = mock(JobBoardUploadService.class);
    private final JobBoardInquiryService jobBoardInquiryService = mock(JobBoardInquiryService.class);
    private final JobBoardUpdateService jobBoardUpdateService = mock(JobBoardUpdateService.class);

    private final JobBoardController jobBoardController = new JobBoardController(
            jobBoardUploadService,
            jobBoardInquiryService,
            jobBoardUpdateService
    );

    @Test
    @DisplayName("구인구직 게시글 업로드 : 성공")
    void uploadJobBoard() {
        // given
        Long userSeq = 1L;
        JobBoardUploadRequest param = new JobBoardUploadRequest(
                1L,
                "title",
                "content",
                0L,
                0L,
                List.of("tag1", "tag2"),
                List.of("attachment1", "attachment2"),
                LocalDateTime.now(),
                Experience.JUNIOR,
                "salary",
                1L,
                PostStatus.NORMAL
        );
        // when
        ResponseEntity<PostApiResponse> result = jobBoardController.uploadJobBoard(userSeq, param);
        // then
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("구인구직 게시글 목록 조회 : 성공")
    void searchJobBoard() {
        // given
        String country = "SOUTH_KOREA";
        String sortingMethod = "CREATED_DATE";
        String industry = "IT";
        String experience = "JUNIOR";
        int page = 0;
        LocalDateTime now = LocalDateTime.now();
        JobBoardResult jobBoardResult = JobBoardResult.builder()
                .seq(1L)
                .title("title")
                .content("content")
                .viewCount(0L)
                .likeCount(0L)
                .tags(Collections.emptyList())
                .attachments(Collections.emptyList())
                .likeUsers(Collections.emptyList())
                .bookmarkUsers(Collections.emptyList())
                .country(Countries.SOUTH_KOREA)
                .region("region")
                .industry(Industry.IT.toPostIndustry())
                .deadline(now)
                .experience(Experience.JUNIOR)
                .salary("salary")
                .companyName("name")
                .companyEmail("email")
                .companyPhone("phone")
                .companyAddress("address")
                .companyHomepage("homepage")
                .companyLogo("logo")
                .companyManagerUserSeq(1L)
                .status(PostStatus.NORMAL)
                .createdAt(now)
                .build();

        when(jobBoardInquiryService.getJobBoards(country, sortingMethod, industry, experience, page))
                .thenReturn(new PageImpl<>(List.of(jobBoardResult)));
        // when
        ResponseEntity<PostApiResponse> result = jobBoardController.searchJobBoard(
                        country, sortingMethod, industry, experience, page
                );
        // then
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("구인구직 게시글 수정 : 성공")
    void updateJobBoard() {
        // given
        Long userSeq = 1L;
        JobBoardUpdateRequest param = new JobBoardUpdateRequest(
                "title",
                "content",
                List.of("tag1", "tag2"),
                List.of("tag3", "tag4"),
                List.of("attachment1", "attachment2"),
                List.of("attachment3", "attachment4"),
                LocalDateTime.now(),
                Experience.JUNIOR,
                "salary"
        );

        // when
        ResponseEntity<Void> result = jobBoardController.updateJobBoard(userSeq, 1L, param);
        // then
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("구인구직 게시글 삭제 : 성공")
    void deleteJobBoard() {
        // given
        Long userSeq = 1L;
        // when
        ResponseEntity<Void> result = jobBoardController.deleteJobBoard(userSeq, 1L);
        // then
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }

}