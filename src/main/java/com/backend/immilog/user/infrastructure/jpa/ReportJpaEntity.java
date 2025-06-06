package com.backend.immilog.user.infrastructure.jpa;

import com.backend.immilog.user.domain.model.report.Report;
import com.backend.immilog.user.domain.model.report.ReportReason;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@DynamicUpdate
@Entity
public class ReportJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq")
    private Long seq;

    @Column(name = "reported_user_seq")
    private Long reportedUserSeq;

    @Column(name = "reporter_user_seq")
    private Long reporterUserSeq;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private ReportReason reason;

    @Column(name = "created_at")
    private final LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected ReportJpaEntity() {}

    protected ReportJpaEntity(
            Long seq,
            Long reportedUserSeq,
            Long reporterUserSeq,
            String description,
            ReportReason reason,
            LocalDateTime updatedAt
    ) {
        this.seq = seq;
        this.reportedUserSeq = reportedUserSeq;
        this.reporterUserSeq = reporterUserSeq;
        this.description = description;
        this.reason = reason;
    }

    public static ReportJpaEntity from(Report report) {
        return new ReportJpaEntity(
                report.seq(),
                report.reportedUserSeq(),
                report.reporterUserSeq(),
                report.description(),
                report.reason(),
                report.seq() == null ? null : report.updatedAt()
        );
    }

    public Report toDomain() {
        return new Report(
                this.seq,
                this.reportedUserSeq,
                this.reporterUserSeq,
                this.description,
                this.reason,
                this.createdAt,
                this.updatedAt
        );
    }
}
