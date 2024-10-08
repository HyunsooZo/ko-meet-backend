package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.domain.model.PostResource;
import com.backend.immilog.post.domain.model.enums.PostType;
import com.backend.immilog.post.domain.model.enums.ResourceType;
import com.backend.immilog.post.domain.repositories.PostResourceRepository;
import com.backend.immilog.post.infrastructure.jpa.entities.QPostResourceEntity;
import com.backend.immilog.post.infrastructure.jpa.repository.PostResourceJpaRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostResourceRepositoryImpl implements PostResourceRepository {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private final PostResourceJpaRepository postResourceJpaRepository;

    @Override
    public void deleteAllEntities(
            Long postSeq,
            PostType postType,
            ResourceType resourceType,
            List<String> deleteAttachments
    ) {
        QPostResourceEntity postResource = QPostResourceEntity.postResourceEntity;
        BooleanExpression criteria = getCriteria(
                postSeq,
                deleteAttachments,
                postType,
                resourceType,
                postResource
        );
        queryFactory.delete(postResource)
                .where(criteria)
                .execute();
    }

    @Override
    public void deleteAllByPostSeq(
            Long seq
    ) {
        QPostResourceEntity postResource = QPostResourceEntity.postResourceEntity;
        JPADeleteClause deleteClause = new JPADeleteClause(entityManager, postResource);
        deleteClause
                .where(postResource.postSeq.eq(seq))
                .execute();
    }

    @Override
    public List<PostResource> findAllByPostSeq(
            Long seq
    ) {
        return postResourceJpaRepository.findAllByPostSeq(seq);
    }

    private static BooleanExpression getCriteria(
            Long postSeq,
            List<String> deleteAttachments,
            PostType postType,
            ResourceType resourceType,
            QPostResourceEntity postResource
    ) {
        return postResource.content.in(deleteAttachments)
                .and(postResource.postSeq.eq(postSeq)
                        .and(postResource.resourceType.eq(resourceType))
                        .and(postResource.postType.eq(postType))
                );
    }
}
