package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.shared.enums.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository {

    Page<Post> getPosts(
            Country country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    );

    Post getPostDetail(String postId);

    Page<Post> getPostsByKeyword(
            String keyword,
            Pageable pageable
    );

    Page<Post> getPostsByUserId(
            String userId,
            Pageable pageable
    );

    Post getById(String postId);

    Post save(Post postEntity);

    List<Post> getPostsByPostIdList(List<String> postIdList);
}