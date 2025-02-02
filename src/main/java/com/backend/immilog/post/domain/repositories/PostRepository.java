package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.global.enums.Country;
import com.backend.immilog.post.domain.enums.Categories;
import com.backend.immilog.post.domain.enums.SortingMethods;
import com.backend.immilog.post.domain.model.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {

    Page<Post> getPosts(
            Country country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    );

    Post getPostDetail(Long postSeq);

    Page<Post> getPostsByKeyword(
            String keyword,
            Pageable pageable
    );

    Page<Post> getPostsByUserSeq(
            Long userSeq,
            Pageable pageable
    );

    Post getById(Long postSeq);

    Post save(Post postEntity);

}