package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.result.PostResult;
import com.backend.immilog.post.application.services.command.PostCommandService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.parameters.P;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PopularPostServiceTest {

    private final PostCommandService postCommandService = mock(PostCommandService.class);
    private final PostInquiryService postInquiryService = mock(PostInquiryService.class);
    private final PopularPostService popularPostService = new PopularPostService(postCommandService, postInquiryService);

    @Test
    @DisplayName("aggregatePopularPosts 메서드가 가장 조회 수 높은 게시글과 인기 게시글 저장")
    void aggregatePopularPostsShouldSaveMostViewedAndHotPosts() throws JsonProcessingException {
        // Given
        List<PostResult> mostViewedPosts = List.of(
                new PostResult(1L, "Most Viewed Post", null, null, null, null, null, null, 1000L, null, null, null, null, null, null, null, null, null, null, null, null, null),
                new PostResult(2L, "Another Viewed Post", null, null, null, null, null, null, 900L, null, null, null, null, null, null, null, null, null, null, null, null, null));
        List<PostResult> hotPosts = List.of(
                new PostResult(3L, 2L, "content", null, null, null, null, null, null, null, null, null, null, null, null, null),
                new PostResult(4L, "Another Hot Post", null, null, null, null, null, null, 700L, null, null, null, null, null, null, null, null, null, null, null, null, null)
        );

        when(postInquiryService.getMostViewedPosts()).thenReturn(mostViewedPosts);
        when(postInquiryService.getHotPosts()).thenReturn(hotPosts);

        // When
        popularPostService.aggregatePopularPosts();

        // Then
        int expectedExpiration = 60 * 60; // 1 hour
        ArgumentCaptor<List<PostResult>> postCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Integer> expirationCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(postCommandService).saveMostViewedPosts(postCaptor.capture(), expirationCaptor.capture());
        verify(postCommandService).saveHotPosts(postCaptor.capture(), expirationCaptor.capture());

        List<List<PostResult>> capturedPosts = postCaptor.getAllValues();
        List<PostResult> capturedMostViewedPosts = capturedPosts.get(0);
        List<PostResult> capturedHotPosts = capturedPosts.get(1);

        assertThat(capturedMostViewedPosts).isEqualTo(mostViewedPosts);
        assertThat(capturedHotPosts).isEqualTo(hotPosts);
        assertThat(expirationCaptor.getAllValues()).containsExactly(expectedExpiration, expectedExpiration);
    }

    @Test
    @DisplayName("aggregatePopularPosts 메서드가 예외 발생 시 로그 출력")
    void aggregatePopularPostsShouldLogErrorWhenExceptionOccurs() throws JsonProcessingException {
        // Given
        when(postInquiryService.getMostViewedPosts()).thenReturn(List.of());
        when(postInquiryService.getHotPosts()).thenReturn(List.of());
        doThrow(new RuntimeException("Failed to save most viewed posts")).when(postCommandService).saveMostViewedPosts(anyList(), anyInt());
        // When
        popularPostService.aggregatePopularPosts();
    }

    @Test
    @DisplayName("aggregatePopularPosts 메서드가 예외 발생 시 로그 출력 (2)")
    void aggregatePopularPostsShouldLogErrorWhenExceptionOccurs2() throws JsonProcessingException {
        // Given
        List<PostResult> mostViewedPosts = List.of(
                new PostResult(1L, "Most Viewed Post", null, null, null, null, null, null, 1000L, null, null, null, null, null, null, null, null, null, null, null, null, null),
                new PostResult(2L, "Another Viewed Post", null, null, null, null, null, null, 900L, null, null, null, null, null, null, null, null, null, null, null, null, null));
        List<PostResult> hotPosts = List.of(
                new PostResult(3L, 2L, "content", null, null, null, null, null, null, null, null, null, null, null, null, null),
                new PostResult(4L, "Another Hot Post", null, null, null, null, null, null, 700L, null, null, null, null, null, null, null, null, null, null, null, null, null)
        );
        when(postInquiryService.getMostViewedPosts()).thenReturn(mostViewedPosts);
        when(postInquiryService.getHotPosts()).thenReturn(hotPosts);

        doThrow(new RuntimeException("Failed to save hot posts")).when(postCommandService).saveHotPosts(anyList(), anyInt());

        popularPostService.aggregatePopularPosts();
    }
}
