package com.backend.komeet.post.presentation.controller;

import com.backend.komeet.global.security.JwtProvider;
import com.backend.komeet.post.model.dtos.JobBoardDto;
import com.backend.komeet.post.presentation.request.JobBoardUploadRequest;
import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.post.application.jobboard.JobBoardDetailService;
import com.backend.komeet.post.application.jobboard.JobBoardSearchService;
import com.backend.komeet.post.application.jobboard.JobBoardUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * 구인구직 게시판 관련 API를 정의한 컨트롤러
 */
@Api(tags = "JobBoard API", description = "구인구직 업로드 관련 API")
@RequestMapping("/api/v1/job-boards")
@RequiredArgsConstructor
@RestController
public class JobBoardController {
    private final JobBoardUploadService jobBoardUploadService;
    private final JobBoardSearchService jobBoardSearchService;
    private final JobBoardDetailService jobBoardDetailService;
    private final JwtProvider jwtProvider;

    /**
     * 구인구직 게시글 업로드
     */
    @PostMapping
    @ApiOperation(value = "구인구직 게시글 업로드", notes = "구인구직 게시글을 업로드합니다.")
    public ResponseEntity<ApiResponse> uploadJobBoard(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestBody JobBoardUploadRequest jobBoardRequest
    ) {

        Long userSeq = jwtProvider.getIdFromToken(token);
        jobBoardUploadService.postJobBoard(jobBoardRequest, userSeq);
        return ResponseEntity.status(OK).body(new ApiResponse(CREATED.value()));
    }

    /**
     * 구인구직 게시글 목록 조회
     */
    @GetMapping
    @ApiOperation(value = "구인구직 게시글 조회", notes = "구인구직 게시글을 조회합니다.")
    public ResponseEntity<ApiResponse> searchJobBoard(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String sortingMethod,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) Integer page
    ) {
        Page<JobBoardDto> jobBoards = jobBoardSearchService.getJobBoards(
                country, sortingMethod, industry, experience, page
        );
        return ResponseEntity.status(OK).body(new ApiResponse(jobBoards));
    }

    /**
     * 구인구직 게시글 상세 조회
     */
    @GetMapping("/{jobBoardSeq}")
    @ApiOperation(value = "구인구직 게시글 상세 조회", notes = "구인구직 게시글을 상세 조회합니다.")
    public ResponseEntity<ApiResponse> getJobBoardDetail(
            @PathVariable Long jobBoardSeq
    ) {
        JobBoardDto jobBoard = jobBoardDetailService.getJobBoardDetail(jobBoardSeq);
        return ResponseEntity.status(OK).body(new ApiResponse(jobBoard));
    }
}
