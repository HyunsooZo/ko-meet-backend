package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.post.PostType;
import com.backend.immilog.post.domain.model.resource.PostResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;

import java.util.List;

public interface PostResourceRepository {
    void deleteAllEntities(
            Long postSeq,
            PostType postType,
            ResourceType resourceType,
            List<String> deleteAttachments
    );

    void deleteAllByPostSeq(Long seq);

    List<PostResource> findAllByPostSeq(Long seq);

    List<PostResource> findAllByPostSeqList(
            List<Long> postSeqList,
            PostType postType
    );
}
