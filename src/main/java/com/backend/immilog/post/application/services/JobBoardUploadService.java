package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.command.JobBoardUploadCommand;
import com.backend.immilog.post.application.services.command.JobBoardCommandService;
import com.backend.immilog.post.domain.model.post.JobBoard;
import com.backend.immilog.user.application.result.CompanyResult;
import com.backend.immilog.user.application.services.CompanyInquiryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobBoardUploadService {
    private final JobBoardCommandService jobBoardCommandService;
    private final CompanyInquiryService companyInquiryService;

    public JobBoardUploadService(
            JobBoardCommandService jobBoardCommandService,
            CompanyInquiryService companyInquiryService
    ) {
        this.jobBoardCommandService = jobBoardCommandService;
        this.companyInquiryService = companyInquiryService;
    }

    public void uploadJobBoard(
            Long userSeq,
            JobBoardUploadCommand command
    ) {
        CompanyResult company = companyInquiryService.getCompany(userSeq);
        JobBoard newJobBoard = createJobBoard(userSeq, command, company);
        jobBoardCommandService.save(newJobBoard);
    }

    private static JobBoard createJobBoard(
            Long userSeq,
            JobBoardUploadCommand command,
            CompanyResult company
    ) {
        return JobBoard.of(
                userSeq,
                company.toDomain(),
                command.title(),
                command.content(),
                command.experience(),
                command.deadline(),
                command.salary()
        );
    }
}
