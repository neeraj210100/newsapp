package org.demo.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.demo.models.News;
import org.demo.models.dto.NewsDTO;
import org.demo.services.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @PostMapping("/createNews")
    public ResponseEntity<News> createNews(@Valid @RequestBody NewsDTO newsDTO) {
        return ResponseEntity.ok(newsService.createNews(newsDTO));
    }

    @DeleteMapping("/deleteNews/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<List<News>> searchNews(@RequestParam String keyword) {
        return ResponseEntity.ok(newsService.searchNews(keyword));
    }

    @GetMapping("/dailyBulletin")
    public ResponseEntity<List<News>> getDailyNews() {
        return ResponseEntity.ok(newsService.getDailyNews());
    }

    @GetMapping("/external")
    public ResponseEntity<List<News>> fetchExternalNews(@RequestParam String query) {
        return ResponseEntity.ok(newsService.fetchNewsFromExternalApi(query));
    }
} 