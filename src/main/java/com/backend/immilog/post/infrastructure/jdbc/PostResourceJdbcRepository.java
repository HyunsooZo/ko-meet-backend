package com.backend.immilog.post.infrastructure.jdbc;

import com.backend.immilog.post.domain.model.post.PostType;
import com.backend.immilog.post.domain.model.resource.PostResource;
import com.backend.immilog.post.domain.model.resource.ResourceType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PostResourceJdbcRepository {
    private final JdbcClient jdbcClient;

    public PostResourceJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void deleteAllEntities(
            Long postSeq,
            PostType postType,
            ResourceType resourceType,
            List<String> deleteAttachments
    ) {
        if(deleteAttachments.isEmpty()) {
            return;
        }
        String inClause = deleteAttachments.stream()
                .map(item -> "?")
                .collect(Collectors.joining(", "));

        String sql = """
                DELETE FROM post_resource_entity
                WHERE post_seq = ?
                AND post_type = ?
                AND resource_type = ?
                AND resource_name IN (%s)
                """.formatted(inClause);

        jdbcClient.sql(sql)
                .param(postSeq)
                .param(postType.toString())
                .param(resourceType.toString())
                .params(deleteAttachments.toArray())
                .update();
    }

    public void deleteAllByPostSeq(Long seq) {
        jdbcClient.sql("""
                        DELETE FROM post_resource
                        WHERE post_seq = ?
                        """)
                .param(seq)
                .update();
    }

    public List<PostResource> findAllByPostSeqList(
            List<Long> postSeqList,
            PostType postType
    ) {
        if (postSeqList.isEmpty()) {
            return List.of();
        }

        String inClause = postSeqList.stream()
                .map(seq -> "?")
                .collect(Collectors.joining(", "));

        String sql = """
                SELECT *
                FROM post_resource
                WHERE post_seq IN (%s)
                AND post_type = ?
                """.formatted(inClause);

        return jdbcClient.sql(sql)
                .params(postSeqList.toArray())
                .param(postType.name())
                .query(PostResource.class)
                .list();
    }
}
