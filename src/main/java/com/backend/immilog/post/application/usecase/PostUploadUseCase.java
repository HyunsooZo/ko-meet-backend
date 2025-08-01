package com.backend.immilog.post.application.usecase;

import com.backend.immilog.post.application.dto.PostUploadCommand;
import com.backend.immilog.post.application.services.BulkCommandService;
import com.backend.immilog.post.application.services.PostCommandService;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.resource.PostResource;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.user.application.services.query.UserQueryService;
import com.backend.immilog.user.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.backend.immilog.post.domain.model.post.PostType.POST;
import static com.backend.immilog.post.domain.model.resource.ResourceType.ATTACHMENT;
import static com.backend.immilog.post.domain.model.resource.ResourceType.TAG;
import static com.backend.immilog.post.exception.PostErrorCode.FAILED_TO_SAVE_POST;

public interface PostUploadUseCase {
    void uploadPost(
            String userId,
            PostUploadCommand postUploadCommand
    );

    @Slf4j
    @Service
    class PostUploader implements PostUploadUseCase {
        private final PostCommandService postCommandService;
        private final UserQueryService userQueryService;
        private final BulkCommandService bulkInsertRepository;

        public PostUploader(
                PostCommandService postCommandService,
                UserQueryService userQueryService,
                BulkCommandService bulkInsertRepository
        ) {
            this.postCommandService = postCommandService;
            this.userQueryService = userQueryService;
            this.bulkInsertRepository = bulkInsertRepository;
        }

        @Override
        @Transactional
        public void uploadPost(
                String userId,
                PostUploadCommand postUploadCommand
        ) {
            final var user = userQueryService.getUserById(userId);
            final var newPost = createPost(postUploadCommand, user);
            final var savedPost = postCommandService.save(newPost);
            this.insertAllPostResources(postUploadCommand, savedPost.id());
        }

        private void insertAllPostResources(
                PostUploadCommand command,
                String postId
        ) {
            final var resourceList = this.getPostResourceList(command, postId);
            bulkInsertRepository.saveAll(
                    resourceList,
                    """
                            INSERT INTO post_resource (
                                post_id,
                                post_type,
                                resource_type,
                                content
                            ) VALUES (?, ?, ?, ?)
                            """,
                    (ps, postResource) -> {
                        try {
                            ps.setString(1, postResource.postId());
                            ps.setString(2, postResource.postType().name());
                            ps.setString(3, postResource.resourceType().name());
                            ps.setString(4, postResource.content());
                        } catch (SQLException e) {
                            log.error("Failed to save post resource: {}", e.getMessage());
                            throw new PostException(FAILED_TO_SAVE_POST);
                        }
                    }
            );
        }

        private List<PostResource> getPostResourceList(
                PostUploadCommand postUploadCommand,
                String postId
        ) {
            var postResources = new ArrayList<PostResource>();
            postResources.addAll(this.getTagEntities(postUploadCommand, postId));
            postResources.addAll(this.getAttachmentEntities(postUploadCommand, postId));
            return Collections.unmodifiableList(postResources);
        }

        private List<PostResource> getTagEntities(
                PostUploadCommand postUploadCommand,
                String postId
        ) {
            if (postUploadCommand.tags() == null) {
                return List.of();
            }
            return postUploadCommand
                    .tags()
                    .stream()
                    .map(tag -> PostResource.of(POST, TAG, tag, postId))
                    .toList();
        }

        private List<PostResource> getAttachmentEntities(
                PostUploadCommand postUploadCommand,
                String postId
        ) {
            if (postUploadCommand.attachments() == null) {
                return List.of();
            }
            return postUploadCommand
                    .attachments()
                    .stream()
                    .map(url -> PostResource.of(POST, ATTACHMENT, url, postId))
                    .toList();
        }

        private static Post createPost(
                PostUploadCommand postUploadCommand,
                User user
        ) {
            return Post.of(
                    user.getUserId().value(),
                    user.getNickname(),
                    user.getCountry(),
                    user.getRegion(),
                    user.getImageUrl(),
                    postUploadCommand.title(),
                    postUploadCommand.content(),
                    postUploadCommand.category(),
                    postUploadCommand.isPublic() ? "Y" : "N"
            );
        }
    }
}

