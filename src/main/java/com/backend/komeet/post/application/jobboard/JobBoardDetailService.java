package com.backend.komeet.post.application.jobboard;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.post.model.dtos.JobBoardDto;
import com.backend.komeet.post.repositories.JobBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.backend.komeet.global.exception.ErrorCode.JOB_BOARD_NOT_FOUND;

/**
 * 구인 게시판 상세 조회 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class JobBoardDetailService {
    private final JobBoardRepository jobBoardRepository;

    /**
     * 구인 게시판 상세 정보를 조회하는 메서드
     */
    public JobBoardDto getJobBoardDetail(
            Long jobBoardSeq
    ) {
        return JobBoardDto.from(
                jobBoardRepository
                        .findById(jobBoardSeq)
                        .orElseThrow(() -> new CustomException(JOB_BOARD_NOT_FOUND))
        );
    }
}
