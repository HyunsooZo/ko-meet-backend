package com.backend.immilog.post.application.services;

import com.backend.immilog.global.enums.Country;
import com.backend.immilog.post.application.result.CommentResult;
import com.backend.immilog.post.application.result.PostResult;
import com.backend.immilog.post.application.services.query.CommentQueryService;
import com.backend.immilog.post.application.services.query.InteractionUserQueryService;
import com.backend.immilog.post.application.services.query.PostQueryService;
import com.backend.immilog.post.domain.enums.Categories;
import com.backend.immilog.post.domain.enums.PostType;
import com.backend.immilog.post.domain.enums.SortingMethods;
import com.backend.immilog.post.domain.model.interaction.InteractionUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PostInquiryService {
    private final PostQueryService postQueryService;
    private final CommentQueryService commentQueryService;
    private final InteractionUserQueryService interactionUserQueryService;

    public PostInquiryService(
            PostQueryService postQueryService,
            CommentQueryService commentQueryService,
            InteractionUserQueryService interactionUserQueryService
    ) {
        this.postQueryService = postQueryService;
        this.commentQueryService = commentQueryService;
        this.interactionUserQueryService = interactionUserQueryService;
    }

    public Page<PostResult> getPosts(
            Country country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Integer page
    ) {
        final Pageable pageable = PageRequest.of(Objects.requireNonNullElse(page, 0), 10);
        return postQueryService.getPosts(
                country,
                sortingMethod,
                isPublic,
                category,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public PostResult getPostDetail(Long postSeq) {
        final PostResult post = postQueryService.getPostDetail(postSeq);
        final List<CommentResult> comments = commentQueryService.getComments(postSeq);
        post.addComments(comments);
        return post;
    }

    @Transactional(readOnly = true)
    public List<PostResult> getBookmarkedPosts(
            Long userSeq,
            PostType postType
    ) {
        final List<InteractionUser> bookmarks = interactionUserQueryService.getBookmarkInteractions(userSeq, postType);
        final List<Long> postSeqList = bookmarks.stream().map(InteractionUser::postSeq).toList();
        return postQueryService.getPostsByPostSeqList(postSeqList);
    }


    public Page<PostResult> searchKeyword(
            String keyword,
            Integer page
    ) {
        final Pageable pageable = PageRequest.of(page, 10);
        final Page<PostResult> posts = postQueryService.getPostsByKeyword(keyword, pageable);
        posts.getContent().forEach(post -> post.addKeywords(keyword));
        return posts;
    }

    public Page<PostResult> getUserPosts(
            Long userSeq,
            Integer page
    ) {
        final Pageable pageable = PageRequest.of(Objects.requireNonNullElse(page, 0), 10);
        return postQueryService.getPostsByUserSeq(userSeq, pageable);
    }

    public List<PostResult> getMostViewedPosts() {
        return postQueryService.getPostsFromRedis("most_viewed_posts");
    }

    public List<PostResult> getHotPosts() {
        return postQueryService.getPostsFromRedis("hot_posts");
    }

}
