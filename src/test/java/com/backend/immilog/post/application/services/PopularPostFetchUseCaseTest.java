package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.usecase.PopularPostFetchUseCase;
import com.backend.immilog.post.application.usecase.PostFetchUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class PopularPostFetchUseCaseTest {

    private final PostCommandService postCommandService = mock(PostCommandService.class);
    private final PostFetchUseCase postFetchUseCase = mock(PostFetchUseCase.class);
    private final PopularPostFetchUseCase popularPostFetchUseCase = new PopularPostFetchUseCase.PopularPostFetcher(postCommandService, postFetchUseCase);

    @Test
    @DisplayName("aggregatePopularPosts 메서드가 예외 발생 시 로그 출력")
    void aggregatePopularPostsShouldLogErrorWhenExceptionOccurs() throws JsonProcessingException {
        // Given
        when(postFetchUseCase.getMostViewedPosts()).thenReturn(List.of());
        when(postFetchUseCase.getHotPosts()).thenReturn(List.of());
        doThrow(new RuntimeException("Failed to save most viewed posts")).when(postCommandService).saveMostViewedPosts(anyList(), anyInt());
        // When
        popularPostFetchUseCase.aggregatePopularPosts();
    }
}
