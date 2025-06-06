package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.post.application.usecase.CommentUploadUseCase;
import com.backend.immilog.post.presentation.request.CommentUploadRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "CommentEntity API", description = "댓글 관련 API")
@RequestMapping("/api/v1/comments")
@RestController
public class CommentController {
    private final CommentUploadUseCase commentUploadUseCase;

    public CommentController(CommentUploadUseCase commentUploadUseCase) {
        this.commentUploadUseCase = commentUploadUseCase;
    }

    @PostMapping("/{referenceType}/{postSeq}/users/{userSeq}")
    @Operation(summary = "댓글 작성", description = "댓글을 작성합니다.")
    public ResponseEntity<Void> createComment(
            @Parameter(description = "레퍼런스 타입") @PathVariable("referenceType") String referenceType,
            @Parameter(description = "게시물 고유번호") @PathVariable("postSeq") Long postSeq,
            @Parameter(description = "사용자 고유번호") @PathVariable("userSeq") Long userSeq,
            @Valid @RequestBody CommentUploadRequest request
    ) {
        commentUploadUseCase.uploadComment(userSeq, postSeq, referenceType, request.content());
        return ResponseEntity.status(CREATED).build();
    }
}
