package com.backend.immilog.post.application;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.post.model.entities.Comment;
import com.backend.immilog.post.model.entities.Post;
import com.backend.immilog.post.model.enums.ReferenceType;
import com.backend.immilog.post.model.repositories.CommentRepository;
import com.backend.immilog.post.model.repositories.PostRepository;
import com.backend.immilog.post.model.services.CommentUploadService;
import com.backend.immilog.post.presentation.request.CommentUploadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.backend.immilog.post.exception.PostErrorCode.POST_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentUploadServiceImpl implements CommentUploadService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public void uploadComment(
            Long userId,
            Long postSeq,
            String referenceType,
            CommentUploadRequest commentUploadRequest
    ) {
        Post post = getPost(postSeq);
        post.setCommentCount(post.getCommentCount() + 1);

        ReferenceType reference = ReferenceType.getByString(referenceType);

        Comment comment = Comment.of(
                userId,
                postSeq,
                commentUploadRequest.content(),
                reference
        );

        commentRepository.save(comment);
    }

    private Post getPost(
            Long postSeq
    ) {
        return postRepository
                .findById(postSeq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }
}
