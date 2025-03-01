package com.backend.immilog.post.infrastructure.jdbc;

import com.backend.immilog.global.enums.Country;
import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.post.application.result.CommentResult;
import com.backend.immilog.post.domain.enums.PostStatus;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.user.application.result.UserInfoResult;
import com.backend.immilog.user.domain.enums.UserStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommentJdbcRepository {
    private final JdbcClient jdbcClient;

    public CommentJdbcRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private static CommentResult getCommentEntityResult(
            ResultSet rs,
            long commentSeq,
            UserInfoResult user
    ) {
        try {
            final String prefix = commentSeq == rs.getLong("c.seq") ? "c" : "cc";
            final String content = rs.getString(prefix + ".content");
            final int likeCount = rs.getInt(prefix + ".like_count");
            final int replyCount = rs.getInt(prefix + ".reply_count");
            final PostStatus status = PostStatus.valueOf(rs.getString(prefix + ".status"));
            final LocalDateTime localDateTime = rs.getTimestamp(prefix + ".created_at").toLocalDateTime();
            return new CommentResult(
                    commentSeq,
                    user,
                    content,
                    new ArrayList<>(),
                    likeCount,
                    0,
                    replyCount,
                    new ArrayList<>(),
                    status,
                    localDateTime.toString()
            );
        } catch (SQLException e) {
            throw new PostException(PostErrorCode.COMMENT_NOT_FOUND);
        }
    }

    private static UserInfoResult getUserInfoResult(
            ResultSet rs,
            String prefix
    ) {
        try {
        return new UserInfoResult(
                rs.getLong(prefix + ".seq"),
                rs.getString(prefix + ".nickname"),
                rs.getString(prefix + ".email"),
                rs.getString(prefix + ".image_url"),
                rs.getLong(prefix + ".reported_count"),
                rs.getDate(prefix + ".reported_date"),
                Country.valueOf(rs.getString(prefix + ".country")),
                Country.valueOf(rs.getString(prefix + ".interest_country")),
                rs.getString(prefix + ".region"),
                UserRole.valueOf(rs.getString(prefix + ".user_role")),
                UserStatus.valueOf(rs.getString(prefix + ".user_status"))
        );
        } catch (SQLException e) {
            throw new PostException(PostErrorCode.COMMENT_NOT_FOUND);
        }
    }

    public List<CommentResult> getComments(Long postSeq) {
        String sql = """
                SELECT c.*, u.*, cc.*, cu.*
                FROM comment c
                LEFT JOIN user u ON c.user_seq = u.seq
                LEFT JOIN comment cc ON cc.post_seq = ?
                                    AND cc.parent_seq = c.seq
                                    AND cc.reference_type = 'COMMENT'
                LEFT JOIN user cu ON cc.user_seq = cu.seq
                WHERE c.post_seq = ?
                    AND c.parent_seq IS NULL
                    AND c.reference_type = 'POST'
                ORDER BY c.created_at DESC
                """;

        return jdbcClient.sql(sql)
                .param(postSeq)
                .param(postSeq)
                .query((rs, rowNum) -> mapParentCommentWithChildren(rs))
                .stream()
                .flatMap(List::stream)
                .toList();
    }

    private List<CommentResult> mapParentCommentWithChildren(ResultSet rs) {
        Map<Long, CommentResult> commentMap = new LinkedHashMap<>();
        try {
            do {
                Long commentSeq = rs.getLong("c.seq");
                commentMap.putIfAbsent(commentSeq, getCommentEntityResult(rs, commentSeq, getUserInfoResult(rs, "u")));

                long childCommentSeq = rs.getLong("cc.seq");
                if (childCommentSeq != 0) {
                    CommentResult childComment = getCommentEntityResult(rs, childCommentSeq, getUserInfoResult(rs, "cu"));
                    commentMap.get(commentSeq).addChildComment(childComment);
                }
            } while (rs.next());
        } catch (Exception e) {
            throw new PostException(PostErrorCode.COMMENT_NOT_FOUND);
        }
        return new ArrayList<>(commentMap.values());
    }
}