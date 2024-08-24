package com.backend.immilog.post.model.repositories;

import com.backend.immilog.post.enums.Categories;
import com.backend.immilog.post.model.dtos.PostDTO;
import com.backend.immilog.post.enums.SortingMethods;
import com.backend.immilog.user.enums.Countries;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepositoryCustom {

    Page<PostDTO> getPosts(
            Countries country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    );

    Optional<PostDTO> getPost(
            Long postSeq
    );

    Page<PostDTO> getPostsByKeyword(
            String keyword,
            Pageable pageable
    );
}