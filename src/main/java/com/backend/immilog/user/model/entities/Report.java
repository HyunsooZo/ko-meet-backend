package com.backend.immilog.user.model.entities;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.user.application.dto.UserReportCommand;
import com.backend.immilog.user.enums.ReportReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class Report extends BaseDateEntity {
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
    public static Report of(
            Long targetUserSeq,
            Long reporterUserSeq,
            UserReportCommand reportUserCommand,
            boolean isOther
    ) {
        String description = isOther ?
                reportUserCommand.description() :
                reportUserCommand.reason().getReason();
        return Report.builder()
                .reportedUserSeq(targetUserSeq)
                .reporterUserSeq(reporterUserSeq)
                .reason(reportUserCommand.reason())
                .description(description)
                .build();
    }
}
