package com.backend.immilog.user.infrastructure.repositories;

import com.backend.immilog.user.domain.model.report.Report;
import com.backend.immilog.user.domain.repositories.ReportRepository;
import com.backend.immilog.user.infrastructure.jpa.entity.report.ReportEntity;
import com.backend.immilog.user.infrastructure.jpa.repositories.ReportJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepositoryImpl implements ReportRepository {
    private final ReportJpaRepository reportJpaRepository;

    public ReportRepositoryImpl(ReportJpaRepository reportJpaRepository) {
        this.reportJpaRepository = reportJpaRepository;
    }

    @Override
    public boolean existsByUserSeqNumbers(
            Long targetUserSeq,
            Long reporterUserSeq
    ) {
        return reportJpaRepository.existsByReportedUserSeqAndReporterUserSeq(
                targetUserSeq,
                reporterUserSeq
        );
    }

    @Override
    public Report save(Report report) {
        return reportJpaRepository.save(ReportEntity.from(report)).toDomain();
    }
}
