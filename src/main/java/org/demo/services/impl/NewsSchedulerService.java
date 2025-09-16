package org.demo.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.repositories.NewsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsSchedulerService {

    private final NewsRepository newsRepository;

    @Scheduled(cron = "0 0 0 * * *") // Runs at midnight every day
    @Transactional
    public void deleteOldNews() {
        log.info("Starting scheduled deletion of old news");
        try {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            int deletedCount = newsRepository.deleteByCreatedAtBefore(thirtyDaysAgo);
            log.info("Successfully deleted {} news items older than 30 days", deletedCount);
        } catch (Exception e) {
            log.error("Error during scheduled deletion of old news: {}", e.getMessage(), e);
            throw e;
        }
    }
} 