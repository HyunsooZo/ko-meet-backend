package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.global.enums.Country;
import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.post.application.result.PostResult;
import com.backend.immilog.post.application.usecase.PostDeleteUseCase;
import com.backend.immilog.post.application.usecase.PostFetchUseCase;
import com.backend.immilog.post.application.usecase.PostUpdateUseCase;
import com.backend.immilog.post.application.usecase.PostUploadUseCase;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import com.backend.immilog.post.presentation.request.PostUploadRequest;
import com.backend.immilog.post.presentation.response.PostPageResponse;
import com.backend.immilog.post.presentation.response.PostSingleResponse;
import com.backend.immilog.user.domain.model.user.UserStatus;
import com.backend.immilog.user.domain.model.user.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@DisplayName("PostController 테스트")
class PostControllerTest {
    private final PostUploadUseCase postUploadUseCase = mock(PostUploadUseCase.class);
    private final PostUpdateUseCase postUpdateUseCase = mock(PostUpdateUseCase.class);
    private final PostDeleteUseCase postDeleteUseCase = mock(PostDeleteUseCase.class);
    private final PostFetchUseCase postFetchUseCase = mock(PostFetchUseCase.class);
    private final PostController postController = new PostController(
            postUploadUseCase,
            postUpdateUseCase,
            postDeleteUseCase,
            postFetchUseCase
    );

    @Test
    @DisplayName("게시물 작성")
    void createPost() {
        // given
        PostUploadRequest postUploadRequest = new PostUploadRequest(
                "title",
                "content",
                List.of("tag1", "tag2"),
                List.of("attachment1", "attachment2"),
                true,
                Categories.COMMUNICATION
        );
        Location location = Location.of(Country.SOUTH_KOREA, "region");
        User user = new User(
                1L,
                Auth.of("email", "password"),
                UserRole.ROLE_USER,
                new ReportData(0L, null),
                Profile.of("user", "image", null),
                location,
                UserStatus.ACTIVE,
                null
        );
        Long userSeq = 1L;

        // when
        ResponseEntity<Void> response = postController.createPost(
                userSeq,
                postUploadRequest
        );

        // then
        verify(postUploadUseCase).uploadPost(user.seq(), postUploadRequest.toCommand());
        assertThat(response.getStatusCode()).isEqualTo(ResponseEntity.status(CREATED).build().getStatusCode());
    }

    @Test
    @DisplayName("게시물 수정")
    void updatePost() {
        // given
        Long postSeq = 1L;
        Long userSeq = 1L;

        //ublic record PostUpdateRequest(
        //        String title,
        //        String content,
        //        List<String> deleteTags,
        //        List<String> addTags,
        //        List<String> deleteAttachments,
        //        List<String> addAttachments,
        //        Boolean isPublic
        //) {
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                "title",
                "content",
                List.of("delete-tag"),
                List.of("tag"),
                List.of("delete-attachment"),
                List.of("attachment"),
                true
        );
        // when
        ResponseEntity<Void> response = postController.updatePost(
                postSeq,
                userSeq,
                postUpdateRequest
        );

        // then
        verify(postUpdateUseCase).updatePost(1L, postSeq, postUpdateRequest.toCommand());
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    @DisplayName("게시물 삭제")
    void deletePost() {
        // given
        Long postSeq = 1L;
        Long userSeq = 1L;

        // when
        ResponseEntity<Void> response = postController.deletePost(
                postSeq,
                userSeq
        );

        //then
        verify(postDeleteUseCase).deletePost(1L, postSeq);
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    @DisplayName("게시물 조회수 증가")
    void increaseViewCount() {
        // given
        Long postSeq = 1L;

        // when
        ResponseEntity<Void> response = postController.increaseViewCount(postSeq);

        // then
        verify(postUpdateUseCase).increaseViewCount(postSeq);
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    @DisplayName("게시물 목록 조회")
    void getPosts() {
        // given
        SortingMethods sortingMethod = SortingMethods.CREATED_DATE;
        String isPublic = "Y";
        Categories category = Categories.ALL;
        Integer page = 0;
        //
//    public PostResult(
//                Long seq,
//                String title,
//                String content,
//                Long userSeq,
//                String userProfileUrl,
//                String userNickName,
//                List< CommentResult > comments,
//                Long commentCount,
//                Long viewCount,
//                Long likeCount,
//                List<String> tags,
//                List<String> attachments,
//                List<Long> likeUsers,
//                List<Long> bookmarkUsers,
//                String isPublic,
//                String country,
//                String region,
//                Categories category,
//                PostStatus status,
//                String createdAt,
//                String updatedAt,
//                String keyword
//        ) {
        PostResult postResult = new PostResult(
                1L,
                "title",
                "content",
                1L,
                "userProfileUrl",
                "userNickName",
                List.of(),
                0L,
                0L,
                0L,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                "Y",
                "SOUTH_KOREA",
                "region",
                Categories.COMMUNICATION,
                null,
                "2021-08-01T:00:00:00",
                "2021-08-01T:00:00:00",
                null
        );
        Page<PostResult> posts = new PageImpl<>(List.of(postResult));
        when(postFetchUseCase.getPosts(
                Country.SOUTH_KOREA,
                sortingMethod,
                isPublic,
                category,
                page
        )).thenReturn(posts);

        // when
        ResponseEntity<PostPageResponse> response = postController.getPosts(
                Country.SOUTH_KOREA,
                sortingMethod,
                isPublic,
                category,
                page
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(((Page<PostResult>) Objects.requireNonNull(response.getBody()).data()).getTotalPages())
                .isEqualTo(1);

    }

    @Test
    @DisplayName("게시물 상세 조회")
    void getPost() {
        // given
        Long postSeq = 1L;
        PostResult postResult = new PostResult(
                1L,
                "title",
                "content",
                1L,
                "userProfileUrl",
                "userNickName",
                List.of(),
                0L,
                0L,
                0L,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                "Y",
                "SOUTH_KOREA",
                "region",
                Categories.COMMUNICATION,
                null,
                "2021-08-01T:00:00:00",
                "2021-08-01T:00:00:00",
                null
        );
        when(postFetchUseCase.getPostDetail(postSeq)).thenReturn(postResult);

        // when
        ResponseEntity<PostSingleResponse> response = postController.getPost(postSeq);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(Objects.requireNonNull(response.getBody()).data()).isEqualTo(postResult);
        assertThat(((PostResult) (response.getBody()).data()).getSeq()).isEqualTo(postSeq);
    }

    @Test
    @DisplayName("게시물 검색")
    void searchPost() {
        // given
        String keyword = "keyword";
        Integer page = 0;
        PostResult postResult = new PostResult(
                1L,
                "title",
                "content",
                1L,
                "userProfileUrl",
                "userNickName",
                List.of(),
                0L,
                0L,
                0L,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                "Y",
                "SOUTH_KOREA",
                "region",
                Categories.COMMUNICATION,
                null,
                "2021-08-01T:00:00:00",
                "2021-08-01T:00:00:00",
                null
        );
        Page<PostResult> posts = new PageImpl<>(List.of(postResult));
        when(postFetchUseCase.searchKeyword(
                keyword,
                page
        )).thenReturn(posts);

        // when
        ResponseEntity<PostPageResponse> response = postController.searchPosts(
                keyword,
                page
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(((Page<PostResult>) Objects.requireNonNull(response.getBody()).data()).getTotalPages())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("사용자 시퀀스로 게시물 목록 조회")
    void getUserPosts() {
        // given
        Long userSeq = 1L;
        Integer page = 0;
        PostResult postResult = new PostResult(
                1L,
                "title",
                "content",
                1L,
                "userProfileUrl",
                "userNickName",
                List.of(),
                0L,
                0L,
                0L,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                "Y",
                "SOUTH_KOREA",
                "region",
                Categories.COMMUNICATION,
                null,
                "2021-08-01T:00:00:00",
                "2021-08-01T:00:00:00",
                null
        );
        Page<PostResult> posts = new PageImpl<>(List.of(postResult));
        when(postFetchUseCase.getUserPosts(
                userSeq,
                page
        )).thenReturn(posts);

        // when
        ResponseEntity<PostPageResponse> response = postController.getUserPosts(
                userSeq,
                page
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(((Page<PostResult>) Objects.requireNonNull(
                response.getBody()).data()).getTotalPages())
                .isEqualTo(1);
    }

}