package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.global.enums.Country;
import com.backend.immilog.post.application.result.PostResult;
import com.backend.immilog.post.application.usecase.PostDeleteUseCase;
import com.backend.immilog.post.application.usecase.PostFetchUseCase;
import com.backend.immilog.post.application.usecase.PostUpdateUseCase;
import com.backend.immilog.post.application.usecase.PostUploadUseCase;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.PostType;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import com.backend.immilog.post.presentation.request.PostUploadRequest;
import com.backend.immilog.post.presentation.response.PostListResponse;
import com.backend.immilog.post.presentation.response.PostPageResponse;
import com.backend.immilog.post.presentation.response.PostSingleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Tag(name = "PostEntity API", description = "게시물 관련 API")
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {
    private final PostUploadUseCase postUploadUseCase;
    private final PostUpdateUseCase postUpdateUseCase;
    private final PostDeleteUseCase postDeleteUseCase;
    private final PostFetchUseCase postFetchUseCase;

    public PostController(
            PostUploadUseCase postUploadUseCase,
            PostUpdateUseCase postUpdateUseCase,
            PostDeleteUseCase postDeleteUseCase,
            PostFetchUseCase postFetchUseCase
    ) {
        this.postUploadUseCase = postUploadUseCase;
        this.postUpdateUseCase = postUpdateUseCase;
        this.postDeleteUseCase = postDeleteUseCase;
        this.postFetchUseCase = postFetchUseCase;
    }

    @PostMapping("/users/{userSeq}")
    @Operation(summary = "게시물 작성", description = "게시물을 작성합니다.")
    public ResponseEntity<Void> createPost(
            @Parameter(description = "사용자 고유번호") @PathVariable("userSeq") Long userSeq,
            @Valid @RequestBody PostUploadRequest postUploadRequest
    ) {
        postUploadUseCase.uploadPost(userSeq, postUploadRequest.toCommand());
        return ResponseEntity.status(CREATED).build();
    }

    @PatchMapping("/{postSeq}/users/{userSeq}")
    @Operation(summary = "게시물 수정", description = "게시물을 수정합니다.")
    public ResponseEntity<Void> updatePost(
            @Parameter(description = "게시물 고유번호") @PathVariable("postSeq") Long postSeq,
            @Parameter(description = "사용자 고유번호") @PathVariable("userSeq") Long userSeq,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest
    ) {
        postUpdateUseCase.updatePost(userSeq, postSeq, postUpdateRequest.toCommand());
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{postSeq}/delete/users/{userSeq}")
    @Operation(summary = "게시물 삭제", description = "게시물을 삭제합니다.")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "게시물 고유번호") @PathVariable("postSeq") Long postSeq,
            @Parameter(description = "사용자 고유번호") @PathVariable("userSeq") Long userSeq
    ) {
        postDeleteUseCase.deletePost(userSeq, postSeq);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{postSeq}/view")
    @Operation(summary = "게시물 조회수 증가", description = "게시물 조회수를 증가시킵니다.")
    public ResponseEntity<Void> increaseViewCount(
            @Parameter(description = "게시물 고유번호") @PathVariable("postSeq") Long postSeq
    ) {
        postUpdateUseCase.increaseViewCount(postSeq);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping
    @Operation(summary = "게시물 목록 조회", description = "게시물 목록을 조회합니다.")
    public ResponseEntity<PostPageResponse> getPosts(
            @Parameter(description = "국가") @RequestParam(required = false, name = "country") Country country,
            @Parameter(description = "정렬 방식") @RequestParam(required = false, name = "sortingMethod") SortingMethods sortingMethod,
            @Parameter(description = "공개 여부") @RequestParam(required = false, name = "isPublic") String isPublic,
            @Parameter(description = "카테고리") @RequestParam(required = false, name = "category") Categories category,
            @Parameter(description = "페이지") @RequestParam(required = false, name = "page") Integer page
    ) {
        Page<PostResult> posts = postFetchUseCase.getPosts(country, sortingMethod, isPublic, category, page);
        return ResponseEntity.status(OK).body(PostPageResponse.of(posts));
    }

    @GetMapping("/{postSeq}")
    @Operation(summary = "게시물 상세 조회", description = "게시물 상세 정보를 조회합다.")
    public ResponseEntity<PostSingleResponse> getPost(
            @Parameter(description = "게시물 고유번호") @PathVariable("postSeq") Long postSeq
    ) {
        PostResult post = postFetchUseCase.getPostDetail(postSeq);
        return ResponseEntity.status(OK).body(post.toResponse());
    }

    @GetMapping("/search")
    @Operation(summary = "게시물 검색", description = "게시물을 검색합니다.")
    public ResponseEntity<PostPageResponse> searchPosts(
            @Parameter(description = "검색어") @RequestParam(name = "keyword") String keyword,
            @Parameter(description = "페이지") @RequestParam(name = "page") Integer page
    ) {
        Page<PostResult> posts = postFetchUseCase.searchKeyword(keyword, page);
        return ResponseEntity.status(OK).body(PostPageResponse.of(posts));
    }

    @GetMapping("/users/{userSeq}/page/{page}")
    @Operation(summary = "사용자 게시물 목록 조회", description = "사용자 게시물 목록을 조회합니다.")
    public ResponseEntity<PostPageResponse> getUserPosts(
            @Parameter(description = "사용자 고유번호") @PathVariable("userSeq") Long userSeq,
            @Parameter(description = "페이지") @PathVariable("page") Integer page
    ) {
        Page<PostResult> posts = postFetchUseCase.getUserPosts(userSeq, page);
        return ResponseEntity.status(OK).body(PostPageResponse.of(posts));
    }

    @GetMapping("/bookmarks")
    @Operation(summary = "북마크한 게시물 조회", description = "북마크한 게시물을 조회합니다.")
    public ResponseEntity<PostListResponse> getBookmarkedPosts(
            @Parameter(description = "사용자 고유번호") @RequestParam(name = "userSeq") Long userSeq,
            @Parameter(description = "포스팅 타입")  @RequestParam(name = "postType") PostType postType
    ) {
        final List<PostResult> posts = postFetchUseCase.getBookmarkedPosts(userSeq, postType);
        return ResponseEntity.status(OK).body(PostListResponse.of(posts));
    }

    @GetMapping("/hot")
    @Operation(summary = "인기 게시물 조회", description = "인기 게시물을 조회합니다.")
    public ResponseEntity<PostListResponse> getHotPosts() {
        List<PostResult> posts = postFetchUseCase.getHotPosts();
        return ResponseEntity.status(OK).body(PostListResponse.of(posts));
    }

    @GetMapping("/most-viewed")
    @Operation(summary = "가장 많이 조회된 게시물 조회", description = "가장 많이 조회된 게시물을 조회합니다.")
    public ResponseEntity<PostListResponse> getMostViewedPosts() {
        List<PostResult> posts = postFetchUseCase.getMostViewedPosts();
        return ResponseEntity.status(OK).body(PostListResponse.of(posts));
    }
}
