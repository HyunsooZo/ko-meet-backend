package com.backend.immilog.report.application.service;

import com.backend.immilog.report.domain.enums.ReportReason;
import com.backend.immilog.report.domain.enums.ReportTargetType;
import com.backend.immilog.report.domain.model.Report;
import com.backend.immilog.report.domain.model.ReportId;
import com.backend.immilog.report.domain.service.ReportCreationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReportService {

    private final ReportCommandService reportCommandService;
    private final ReportQueryService reportQueryService;
    private final ReportCreationService reportCreationService;

    public ReportService(
            ReportCommandService reportCommandService,
            ReportQueryService reportQueryService,
            ReportCreationService reportCreationService
    ) {
        this.reportCommandService = reportCommandService;
        this.reportQueryService = reportQueryService;
        this.reportCreationService = reportCreationService;
    }

    public ReportId report(
            String targetUserId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        if (reportQueryService.existsByTargetAndReporter(ReportTargetType.USER, targetUserId, reporterId)) {
            throw new RuntimeException("Already reported");
        }

        var report = reportCreationService.createUserReport(targetUserId, reporterId, reason, customDescription);
        var savedReport = reportCommandService.save(report);
        return savedReport.getId();
    }

    public ReportId reportPost(
            String postId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        if (reportQueryService.existsByTargetAndReporter(ReportTargetType.POST, postId, reporterId)) {
            throw new RuntimeException("Already reported");
        }

        var report = reportCreationService.createPostReport(postId, reporterId, reason, customDescription);
        var savedReport = reportCommandService.save(report);
        return savedReport.getId();
    }

    public ReportId reportComment(
            String commentId,
            String reporterId,
            ReportReason reason,
            String customDescription
    ) {
        if (reportQueryService.existsByTargetAndReporter(ReportTargetType.COMMENT, commentId, reporterId)) {
            throw new RuntimeException("Already reported");
        }

        var report = reportCreationService.createCommentReport(commentId, reporterId, reason, customDescription);
        var savedReport = reportCommandService.save(report);
        return savedReport.getId();
    }

    public void processReport(ReportId reportId) {
        var report = reportQueryService.getById(reportId);
        var processedReport = reportCreationService.processReport(report);
        reportCommandService.save(processedReport);
    }

    public void resolveReport(ReportId reportId) {
        var report = reportQueryService.getById(reportId);
        var resolvedReport = reportCreationService.resolveReport(report);
        reportCommandService.save(resolvedReport);
    }

    public void rejectReport(ReportId reportId) {
        var report = reportQueryService.getById(reportId);
        var rejectedReport = reportCreationService.rejectReport(report);
        reportCommandService.save(rejectedReport);
    }

    @Transactional(readOnly = true)
    public long getReportCountByUser(String userId) {
        return reportQueryService.countByTarget(ReportTargetType.USER, userId);
    }

    @Transactional(readOnly = true)
    public long getReportCountByReporter(String reporterId) {
        return reportQueryService.countByReporter(reporterId);
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsByUser(String userId) {
        return reportQueryService.findByTarget(ReportTargetType.USER, userId);
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsByReporter(String reporterId) {
        return reportQueryService.findByReporter(reporterId);
    }

    @Transactional(readOnly = true)
    public List<Report> getPendingReports() {
        return reportQueryService.findPendingReports();
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsUnderReview() {
        return reportQueryService.findReportsUnderReview();
    }

    @Transactional(readOnly = true)
    public Report getReportById(ReportId reportId) {
        return reportQueryService.getById(reportId);
    }
}