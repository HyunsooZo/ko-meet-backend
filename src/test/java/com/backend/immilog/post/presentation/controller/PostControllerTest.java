package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.post.model.dtos.PostDTO;
import com.backend.immilog.post.model.enums.Categories;
import com.backend.immilog.post.model.enums.SortingMethods;
import com.backend.immilog.post.model.services.PostDeleteService;
import com.backend.immilog.post.model.services.PostInquiryService;
import com.backend.immilog.post.model.services.PostUpdateService;
import com.backend.immilog.post.model.services.PostUploadService;
import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import com.backend.immilog.post.presentation.request.PostUploadRequest;
import com.backend.immilog.user.model.embeddables.Location;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.model.enums.UserCountry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@DisplayName("PostController 테스트")
class PostControllerTest {
    @Mock
    private PostUploadService postUploadService;
    @Mock
    private PostUpdateService postUpdateService;
    @Mock
    private PostDeleteService postDeleteService;
    @Mock
    private PostInquiryService postInquiryService;
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postController = new PostController(
                postUploadService,
                postUpdateService,
                postDeleteService,
                postInquiryService
        );
    }

    @Test
    @DisplayName("게시물 작성")
    void createPost() {
        // given
        PostUploadRequest postUploadRequest = PostUploadRequest.builder()
                .category(Categories.COMMUNICATION)
                .title("title")
                .content("content")
                .isPublic(true)
                .build();
        Location location = Location.builder()
                .country(UserCountry.SOUTH_KOREA)
                .region("region")
                .build();
        User user = User.builder()
                .seq(1L)
                .location(location)
                .build();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userSeq")).thenReturn(1L);

        // when
        ResponseEntity<ApiResponse> response = postController.createPost(
                request,
                postUploadRequest
        );

        // then
        verify(postUploadService).uploadPost(user.getSeq(), postUploadRequest);
        assertThat(response.getStatusCode()).isEqualTo(ResponseEntity.status(CREATED).build().getStatusCode());
    }

    @Test
    @DisplayName("게시물 수정")
    void updatePost() {
        // given
        Long postSeq = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userSeq")).thenReturn(1L);
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .addAttachments(List.of("attachment"))
                .deleteAttachments(List.of("delete-attachment"))
                .addTags(List.of("tag"))
                .deleteTags(List.of("delete-tag"))
                .title("title")
                .content("content")
                .isPublic(true)
                .build();

        // when
        ResponseEntity<ApiResponse> response = postController.updatePost(
                postSeq,
                request,
                postUpdateRequest
        );

        // then
        verify(postUpdateService).updatePost(1L, postSeq, postUpdateRequest);
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    @DisplayName("게시물 삭제")
    void deletePost() {
        // given
        Long postSeq = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userSeq")).thenReturn(1L);

        // when
        ResponseEntity<Void> response = postController.deletePost(
                postSeq,
                request
        );

        //then
        verify(postDeleteService).deletePost(1L, postSeq);
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    @DisplayName("게시물 조회수 증가")
    void increaseViewCount() {
        // given
        Long postSeq = 1L;

        // when
        ResponseEntity<ApiResponse> response =
                postController.increaseViewCount(postSeq);

        // then
        verify(postUpdateService).increaseViewCount(postSeq);
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    @DisplayName("게시물 좋아요")
    void likePost() {
        // given
        Long postSeq = 1L;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userSeq")).thenReturn(1L);

        // when
        ResponseEntity<ApiResponse> response = postController.likePost(
                postSeq,
                request
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
        verify(postUpdateService).likePost(1L, postSeq);
    }

    @Test
    @DisplayName("게시물 목록 조회")
    void getPosts() {
        // given
        SortingMethods sortingMethod = SortingMethods.CREATED_DATE;
        String isPublic = "Y";
        Categories category = Categories.ALL;
        Integer page = 0;
        PostDTO postDTO = mock(PostDTO.class);
        Page<PostDTO> posts = new PageImpl<>(List.of(postDTO));
        when(postInquiryService.getPosts(
                com.backend.immilog.post.model.enums.Countries.SOUTH_KOREA,
                sortingMethod,
                isPublic,
                category,
                page
        )).thenReturn(posts);

        // when
        ResponseEntity<ApiResponse> response = postController.getPosts(
                com.backend.immilog.post.model.enums.Countries.SOUTH_KOREA,
                sortingMethod,
                isPublic,
                category,
                page
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(((Page<PostDTO>) Objects.requireNonNull(response.getBody()).data()).getTotalPages())
                .isEqualTo(1);

    }

    @Test
    @DisplayName("게시물 상세 조회")
    void getPost() {
        // given
        Long postSeq = 1L;
        PostDTO postDTO = mock(PostDTO.class);
        when(postInquiryService.getPost(postSeq)).thenReturn(postDTO);
        when(postDTO.getSeq()).thenReturn(postSeq);

        // when
        ResponseEntity<ApiResponse> response = postController.getPost(postSeq);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(Objects.requireNonNull(response.getBody()).data()).isEqualTo(postDTO);
        assertThat(((PostDTO) (response.getBody()).data()).getSeq()).isEqualTo(postSeq);
    }

    @Test
    @DisplayName("게시물 검색")
    void searchPost() {
        // given
        String keyword = "keyword";
        Integer page = 0;
        PostDTO postDTO = mock(PostDTO.class);
        Page<PostDTO> posts = new PageImpl<>(List.of(postDTO));
        when(postInquiryService.searchKeyword(
                keyword,
                page
        )).thenReturn(posts);

        // when
        ResponseEntity<ApiResponse> response = postController.searchPosts(
                keyword,
                page
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(((Page<PostDTO>) Objects.requireNonNull(response.getBody()).data()).getTotalPages())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("사용자 시퀀스로 게시물 목록 조회")
    void getUserPosts() {
        // given
        Long userSeq = 1L;
        Integer page = 0;
        PostDTO postDTO = mock(PostDTO.class);
        Page<PostDTO> posts = new PageImpl<>(List.of(postDTO));
        when(postInquiryService.getUserPosts(
                userSeq,
                page
        )).thenReturn(posts);

        // when
        ResponseEntity<ApiResponse> response = postController.getUserPosts(
                userSeq,
                page
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(((Page<PostDTO>) Objects.requireNonNull(
                response.getBody()).data()).getTotalPages())
                .isEqualTo(1);
    }

}