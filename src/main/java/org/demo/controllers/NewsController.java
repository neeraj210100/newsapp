package org.demo.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.models.News;
import org.demo.models.dto.NewsDTO;
import org.demo.services.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @DeleteMapping("/deleteNews/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        log.debug("Received request to delete news with ID: {}", id);
        try {
            newsService.deleteNews(id);
            log.debug("Successfully deleted news with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting news with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<News>> searchNews(@RequestParam String keyword) {
        log.debug("Received request to search news with keyword: {}", keyword);
        try {
            List<News> results = newsService.searchNews(keyword);
            log.debug("Found {} news items matching keyword: {}", results.size(), keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error searching news with keyword {}: {}", keyword, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/dailyBulletin")
    public ResponseEntity<List<News>> getDailyNews(
            @RequestParam(defaultValue = "en") String targetLanguage) {
        log.debug("Received request to get daily news bulletin in language: {}", targetLanguage);
        try {
            List<News> dailyNews = newsService.getDailyNews(targetLanguage);
            log.debug("Retrieved {} news items for daily bulletin", dailyNews.size());
            return ResponseEntity.ok(dailyNews);
        } catch (Exception e) {
            log.error("Error getting daily news bulletin: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/external")
    public ResponseEntity<List<News>> fetchExternalNews(
            @RequestParam String query,
            @RequestParam(defaultValue = "en") String targetLanguage) {
        log.debug("Received request to fetch and save external news with query: {} in language: {}", 
                 query, targetLanguage);
        try {
            List<News> externalNews = newsService.fetchNewsFromExternalApi(query, targetLanguage);
            log.debug("Retrieved and saved {} news items from external API", externalNews.size());
            return ResponseEntity.ok(externalNews);
        } catch (Exception e) {
            log.error("Error fetching and saving external news with query {}: {}", query, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllNews() {
        log.debug("Received request to delete all news");
        try {
            newsService.deleteAllNews();
            log.debug("Successfully deleted all news");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting all news: {}", e.getMessage(), e);
            throw e;
        }
    }

} 