package com.backend.immilog.comment.application.usecase;

import com.backend.immilog.comment.application.dto.CommentCreateCommand;
import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.application.services.CommentCommandService;
import com.backend.immilog.comment.domain.model.Comment;
import com.backend.immilog.shared.application.event.DomainEventPublisher;
import org.springframework.stereotype.Service;

public interface CommentCreateUseCase {
    CommentResult createComment(CommentCreateCommand command);

    @Service
    class CommentCreator implements CommentCreateUseCase {
        private final CommentCommandService commentCommandService;
        private final DomainEventPublisher domainEventPublisher;

        public CommentCreator(
                CommentCommandService commentCommandService,
                DomainEventPublisher domainEventPublisher
        ) {
            this.commentCommandService = commentCommandService;
            this.domainEventPublisher = domainEventPublisher;
        }

        @Override
        public CommentResult createComment(CommentCreateCommand command) {
            var comment = Comment.of(
                    command.userId(),
                    command.postId(),
                    command.content(),
                    command.referenceType()
            );
            var savedComment = commentCommandService.createComment(comment);
            
            // 도메인 이벤트 발행
            savedComment.publishCreatedEvent();
            
            // 도메인 이벤트 처리
            domainEventPublisher.publishEvents();
            
            return CommentResult.from(savedComment);
        }
    }
}