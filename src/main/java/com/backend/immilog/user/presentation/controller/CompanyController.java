package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.user.application.result.CompanyResult;
import com.backend.immilog.user.application.services.CompanyInquiryService;
import com.backend.immilog.user.application.services.CompanyRegisterService;
import com.backend.immilog.user.presentation.request.CompanyRegisterRequest;
import com.backend.immilog.user.presentation.response.UserGeneralResponse;
import com.backend.immilog.user.presentation.response.UserCompanyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Company API", description = "회사정보 관련 API")
@RequestMapping("/api/v1/companies")
@RestController
public class CompanyController {
    private final CompanyRegisterService companyRegisterService;
    private final CompanyInquiryService companyInquiryService;

    public CompanyController(
            CompanyRegisterService companyRegisterService,
            CompanyInquiryService companyInquiryService
    ) {
        this.companyRegisterService = companyRegisterService;
        this.companyInquiryService = companyInquiryService;
    }

    @PostMapping("/users/{userSeq}")
    @Operation(summary = "회사정보 등록", description = "회사정보를 등록합니다.")
    public ResponseEntity<UserGeneralResponse> registerCompany(
            @Parameter(description = "사용자 고유번호") @PathVariable("userSeq") Long userSeq,
            @RequestBody CompanyRegisterRequest param
    ) {
        companyRegisterService.registerOrEditCompany(userSeq, param.toCommand());
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping("/users/{userSeq}")
    @Operation(summary = "본인 회사정보 조회", description = "본인 회사정보를 조회합니다.")
    public ResponseEntity<UserCompanyResponse> getCompany(
            @Parameter(description = "사용자 고유번호") @PathVariable("userSeq") Long userSeq
    ) {
        final CompanyResult result = companyInquiryService.getCompany(userSeq);
        return ResponseEntity.status(OK).body(result.toResponse());
    }
}
