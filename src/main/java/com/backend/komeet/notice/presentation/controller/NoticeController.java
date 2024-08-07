package com.backend.komeet.notice.presentation.controller;

import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.global.security.JwtProvider;
import com.backend.komeet.notice.application.NoticeInquiryService;
import com.backend.komeet.notice.application.NoticeModifyService;
import com.backend.komeet.notice.application.NoticeRegisterService;
import com.backend.komeet.notice.presentation.request.NoticeModifyRequest;
import com.backend.komeet.notice.presentation.request.NoticeRegisterRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.backend.komeet.post.enums.PostStatus.DELETED;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

/**
 * 공지사항 관련 컨트롤러
 */
@Api(tags = "Notice API", description = "공지사항 관련 API")
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
@RestController
public class NoticeController {
    private final NoticeRegisterService noticeRegisterService;
    private final NoticeInquiryService noticeInquiryService;
    private final NoticeModifyService noticeModifyService;
    private final JwtProvider jwtProvider;

    @PostMapping
    @ApiOperation(value = "공지사항 등록", notes = "공지사항을 등록합니다.")
    public ResponseEntity<ApiResponse> registerNotice(
            @RequestBody NoticeRegisterRequest request,
            @RequestHeader(AUTHORIZATION) String token
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        noticeRegisterService.registerNotice(userSeq, request);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(true));
    }

    @GetMapping
    @ApiOperation(value = "공지사항 조회", notes = "공지사항을 조회합니다.")
    public ResponseEntity<ApiResponse> getNotices(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam Integer page
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(
                        noticeInquiryService.getNotices(userSeq, page)
                ));
    }

    @GetMapping("/{noticeSeq}")
    @ApiOperation(value = "공지사항 상세 조회", notes = "공지사항을 상세 조회합니다.")
    public ResponseEntity<ApiResponse> getNotice(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long noticeSeq
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(
                        noticeInquiryService.getNotice(userSeq, noticeSeq)
                ));
    }

    @PatchMapping("/{noticeSeq}")
    @ApiOperation(value = "공지사항 수정", notes = "공지사항을 수정합니다.")
    public ResponseEntity<Void> modifyNotice(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long noticeSeq,
            @RequestBody NoticeModifyRequest request
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        noticeModifyService.modifyNotice(userSeq, noticeSeq, request);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{noticeSeq}/delete")
    @ApiOperation(value = "공지사항 삭제", notes = "공지사항을 삭제합니다.")
    public ResponseEntity<Void> deleteNotice(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long noticeSeq
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        noticeModifyService.modifyNotice(
                userSeq,
                noticeSeq,
                NoticeModifyRequest.builder().status(DELETED).build()
        );
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping("/unread")
    @ApiOperation(value = "공지사항 존재 여부 조회", notes = "공지사항이 존재하는지 여부를 조회합니다.")
    public ResponseEntity<ApiResponse> isNoticeExist(
            @RequestHeader(AUTHORIZATION) String token
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(noticeInquiryService.isNoticeExist(userSeq)));
    }

}
