package com.backend.komeet.user.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.user.enums.ReportReason;
import com.backend.komeet.user.presentation.request.UserReportRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 신고 엔티티
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private Long reportedUserSeq;
    private Long reporterUserSeq;
    private String description;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    /**
     * 신고 엔티티 팩토리 메서드
     */
    public static Report from(
            Long targetUserSeq,
            Long reporterUserSeq,
            UserReportRequest reportUserRequest,
            boolean isOther
    ) {
        return Report.builder()
                .reportedUserSeq(targetUserSeq)
                .reporterUserSeq(reporterUserSeq)
                .reason(reportUserRequest.getReason())
                .description(
                        isOther ?
                                reportUserRequest.getDescription() :
                                reportUserRequest.getReason().getReason()
                )
                .build();
    }
}
