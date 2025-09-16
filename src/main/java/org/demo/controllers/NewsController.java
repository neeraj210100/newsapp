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

//    @PostMapping("/createNews")
//    public ResponseEntity<News> createNews(@Valid @RequestBody NewsDTO newsDTO) {
//        log.debug("Received request to create news: {}", newsDTO.getTitle());
//        try {
//            News createdNews = newsService.createNews(newsDTO);
//            log.debug("Successfully created news with ID: {}", createdNews.getId());
//            return ResponseEntity.ok(createdNews);
//        } catch (Exception e) {
//            log.error("Error creating news: {}", e.getMessage(), e);
//            throw e;
//        }
//    }

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
    public ResponseEntity<List<News>> getDailyNews() {
        log.debug("Received request to get daily news bulletin");
        try {
            List<News> dailyNews = newsService.getDailyNews();
            log.debug("Retrieved {} news items for daily bulletin", dailyNews.size());
            return ResponseEntity.ok(dailyNews);
        } catch (Exception e) {
            log.error("Error getting daily news bulletin: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/external")
    public ResponseEntity<List<News>> fetchExternalNews(@RequestParam String query) {
        log.debug("Received request to fetch and save external news with query: {}", query);
        try {
            List<News> externalNews = newsService.fetchNewsFromExternalApi(query);
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

//    @PostMapping("/{id}/translate")
//    public ResponseEntity<News> translateNews(
//            @PathVariable Long id,
//            @RequestParam String targetLanguage) {
//        log.debug("Received request to translate news ID {} to language: {}", id, targetLanguage);
//        try {
//            News translatedNews = newsService.translateNews(id, targetLanguage);
//            log.debug("Successfully translated news ID {} to {}", id, targetLanguage);
//            return ResponseEntity.ok(translatedNews);
//        } catch (Exception e) {
//            log.error("Error translating news ID {} to {}: {}", id, targetLanguage, e.getMessage(), e);
//            throw e;
//        }
//    }
} 