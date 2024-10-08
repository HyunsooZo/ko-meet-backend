package com.backend.immilog.post.application;

import com.backend.immilog.post.application.result.PostResult;
import com.backend.immilog.post.application.services.PostInquiryService;
import com.backend.immilog.post.domain.model.enums.Categories;
import com.backend.immilog.post.domain.model.enums.Countries;
import com.backend.immilog.post.domain.model.enums.SortingMethods;
import com.backend.immilog.post.domain.repositories.CommentRepository;
import com.backend.immilog.post.domain.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("PostInquiryService 테스트")
class PostInquiryServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    private PostInquiryService postInquiryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postInquiryService = new PostInquiryService(
                postRepository,
                commentRepository
        );
    }

    @Test
    @DisplayName("게시물 조회 - 성공")
    void getPosts() {
        // given
        Countries country = Countries.SOUTH_KOREA;
        SortingMethods sortingMethod = SortingMethods.CREATED_DATE;
        String isPublic = "Y";
        Categories category = Categories.ALL;
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        PostResult postResult = PostResult.builder().build();
        Page<PostResult> posts = new PageImpl<>(List.of(postResult));
        when(postRepository.getPosts(
                country,
                sortingMethod,
                isPublic,
                category,
                pageable
        )).thenReturn(posts);
        // when
        Page<PostResult> result = postInquiryService.getPosts(
                country,
                sortingMethod,
                isPublic,
                category,
                page
        );
        // then
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시물 조회(단일 게시물)")
    void getPost() {
        // given
        Long postSeq = 1L;
        PostResult postResult = PostResult.builder().build();
        when(postRepository.getPost(postSeq)).thenReturn(java.util.Optional.of(postResult));
        when(commentRepository.getComments(postSeq)).thenReturn(List.of());
        // when
        PostResult result = postInquiryService.getPost(postSeq);
        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("게시물 검색 - 성공")
    void searchKeyword() {
        // given
        String keyword = "keyword";
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        PostResult postResult = PostResult.builder()
                .content("keyword123")
                .title("content123")
                .tags(List.of("tag1", "tag2"))
                .build();
        Page<PostResult> posts = new PageImpl<>(List.of(postResult));
        when(postRepository.getPostsByKeyword(keyword, pageable)).thenReturn(posts);
        // when
        Page<PostResult> result = postInquiryService.searchKeyword(keyword, page);
        // then
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자 게시물 조회 - 성공")
    void getUserPosts() {
        // given
        Long userSeq = 1L;
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        PostResult postResult = PostResult.builder().build();
        Page<PostResult> posts = new PageImpl<>(List.of(postResult));
        when(postRepository.getPostsByUserSeq(userSeq, pageable)).thenReturn(posts);
        // when
        Page<PostResult> result = postInquiryService.getUserPosts(userSeq, page);
        // then
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

}