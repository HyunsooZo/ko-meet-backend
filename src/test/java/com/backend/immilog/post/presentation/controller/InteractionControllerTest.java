package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.post.application.services.InteractionCreationService;
import com.backend.immilog.post.domain.enums.InteractionType;
import com.backend.immilog.post.domain.enums.PostType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("InteractionController 테스트")
class InteractionControllerTest {
    private final InteractionCreationService interactionCreationService = mock(InteractionCreationService.class);
    private final InteractionController interactionController = new InteractionController(interactionCreationService);

    @Test
    @DisplayName("인터랙션 등록")
    void createInteraction() {
        // given
        PostType postType = PostType.POST;
        InteractionType interactionType = InteractionType.LIKE;
        Long postSeq = 1L;
        Long userSeq = 1L;

        // when
        interactionController.createInteraction(postSeq, interactionType, postType, userSeq);
        // then
        verify(interactionCreationService).createInteraction(userSeq, postSeq, postType, interactionType);
    }
}