package com.backend.immilog.notice.presentation;

import com.backend.immilog.notice.application.service.NoticeQueryService;
import com.backend.immilog.notice.application.usecase.*;
import com.backend.immilog.notice.domain.model.NoticeId;
import com.backend.immilog.shared.annotation.CurrentUser;
import com.backend.immilog.shared.enums.Country;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notice API", description = "공지사항 관련 API")
@RequestMapping("/api/v1/notices")
@RestController
public class NoticeController {
    private final CreateNoticeUseCase createNoticeUseCase;
    private final NoticeQueryService noticeQueryService;
    private final GetNoticeUseCase getNoticeUseCase;
    private final UpdateNoticeUseCase updateNoticeUseCase;
    private final DeleteNoticeUseCase deleteNoticeUseCase;
    private final MarkNoticeAsReadUseCase markNoticeAsReadUseCase;

    public NoticeController(
            CreateNoticeUseCase createNoticeUseCase,
            NoticeQueryService noticeQueryService,
            GetNoticeUseCase getNoticeUseCase,
            UpdateNoticeUseCase updateNoticeUseCase,
            DeleteNoticeUseCase deleteNoticeUseCase,
            MarkNoticeAsReadUseCase markNoticeAsReadUseCase
    ) {
        this.createNoticeUseCase = createNoticeUseCase;
        this.noticeQueryService = noticeQueryService;
        this.getNoticeUseCase = getNoticeUseCase;
        this.updateNoticeUseCase = updateNoticeUseCase;
        this.deleteNoticeUseCase = deleteNoticeUseCase;
        this.markNoticeAsReadUseCase = markNoticeAsReadUseCase;
    }

    @PostMapping
    @Operation(summary = "공지사항 등록", description = "공지사항을 등록합니다.")
    public ResponseEntity<NoticeRegistrationResponse> registerNotice(
            @CurrentUser String userId,
            @RequestBody NoticeRegisterRequest noticeRegisterRequest
    ) {
        var command = noticeRegisterRequest.toCommand();
        createNoticeUseCase.execute(
                userId, // 이제 userId 사용
                command.title(),
                command.content(),
                command.type(),
                command.targetCountry()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(NoticeRegistrationResponse.success());
    }

    @GetMapping("users/{userId}")
    @Operation(summary = "공지사항 조회", description = "공지사항을 조회합니다.")
    public ResponseEntity<NoticeListResponse> getNotices(
            @Parameter(description = "사용자 고유번호") @PathVariable("userId") String userId,
            @Parameter(description = "페이지 번호") @RequestParam("page") Integer page
    ) {
        var pageable = PageRequest.of(page, 20);
        var notices = noticeQueryService.getNotices(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(NoticeListResponse.of(notices));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 상제정보를 조회합니다.")
    public ResponseEntity<NoticeDetailResponse> getNoticeDetail(
            @Parameter(description = "공지사항 고유번호") @PathVariable("noticeId") String noticeId
    ) {
        var notice = getNoticeUseCase.execute(noticeId);
        return ResponseEntity.status(HttpStatus.OK).body(NoticeDetailResponse.of(notice));
    }

    @GetMapping("/users/{userId}/unread")
    @Operation(summary = "공지사항 존재 여부 조회", description = "공지사항이 존재하는지 여부를 조회합니다.")
    public ResponseEntity<NoticeRegistrationResponse> isNoticeExist(
            @Parameter(description = "사용자 고유번호") @PathVariable("userId") String userId,
            @Parameter(description = "사용자 국가") @RequestParam("country") Country country
    ) {
        var unreadNoticeExist = noticeQueryService.areUnreadNoticesExist(country, userId);
        return ResponseEntity.status(HttpStatus.OK).body(NoticeRegistrationResponse.of(unreadNoticeExist));
    }

    @PatchMapping("/{noticeId}")
    @Operation(summary = "공지사항 수정", description = "공지사항을 수정합니다.")
    public ResponseEntity<Void> modifyNotice(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(description = "공지사항 고유번호") @PathVariable("noticeId") String noticeId,
            @Parameter(description = "공지사항 수정바디") @RequestBody NoticeModifyRequest param
    ) {
        updateNoticeUseCase.execute(
                token,
                NoticeId.of(noticeId),
                param.title(),
                param.content(),
                param.type()
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{noticeId}/users/{userId}")
    @Operation(summary = "공지사항 읽음처리", description = "공지사항을 읽음 처리합니다.")
    public ResponseEntity<Void> readNotice(
            @Parameter(description = "공지사항 고유번호") @PathVariable("noticeId") String noticeId,
            @Parameter(description = "사용자 고유번호") @PathVariable("userId") String userId,
            @Parameter(description = "사용자 국가") @RequestParam("country") Country userCountry
    ) {
        markNoticeAsReadUseCase.execute(noticeId, userId, userCountry);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{noticeId}")
    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.")
    public ResponseEntity<Void> deleteNotice(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(description = "공지사항 고유번호") @PathVariable("noticeId") String noticeId
    ) {
        deleteNoticeUseCase.execute(token, noticeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}