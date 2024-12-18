package com.backend.immilog.post.application.services.command;

import com.backend.immilog.global.aop.PerformanceMonitor;
import com.backend.immilog.post.domain.repositories.BulkInsertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.function.BiConsumer;

@Service
public class BulkCommandService {
    private final BulkInsertRepository bulkInsertRepository;

    public BulkCommandService(BulkInsertRepository bulkInsertRepository) {
        this.bulkInsertRepository = bulkInsertRepository;
    }

    @PerformanceMonitor
    @Transactional
    public <T> void saveAll(
            List<T> entities,
            String command,
            BiConsumer<PreparedStatement, T> failedToSavePostResource
    ) {
        bulkInsertRepository.saveAll(
                entities,
                command,
                failedToSavePostResource
        );
    }
}
