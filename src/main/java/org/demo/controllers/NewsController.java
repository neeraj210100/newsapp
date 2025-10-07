package org.demo.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "News Management", description = "APIs for managing news articles, including search, categorization, and external news fetching")
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "Search news articles", description = "Search for news articles by keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully",
                    content = @Content(schema = @Schema(implementation = News.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search keyword"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<List<News>> searchNews(
            @Parameter(description = "Keyword to search for in news articles", required = true)
            @RequestParam String keyword) {
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


    @Operation(summary = "Fetch external news", description = "Fetch and save news articles from external API based on query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "External news fetched and saved successfully",
                    content = @Content(schema = @Schema(implementation = News.class))),
            @ApiResponse(responseCode = "400", description = "Invalid query or language code"),
            @ApiResponse(responseCode = "500", description = "Internal server error or external API failure")
    })
    @GetMapping("/external")
    public ResponseEntity<List<News>> fetchExternalNews(
            @Parameter(description = "Search query for external news API", required = true)
            @RequestParam String query,
            @Parameter(description = "Target language for translation (default: en)", example = "en")
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


    @Operation(summary = "Get daily news bulletin", description = "Retrieve the daily news bulletin in the specified language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daily news bulletin retrieved successfully",
                    content = @Content(schema = @Schema(implementation = News.class))),
            @ApiResponse(responseCode = "400", description = "Invalid language code"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/dailyBulletin")
    public ResponseEntity<List<News>> getDailyNews(
            @Parameter(description = "Target language for translation (default: en)", example = "en")
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
    // Category-related endpoints
    @Operation(summary = "Get news by category", description = "Retrieve the latest news articles for a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category news retrieved successfully",
                    content = @Content(schema = @Schema(implementation = News.class))),
            @ApiResponse(responseCode = "400", description = "Invalid category or parameters"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/dailyBulletin/{category}")
    public ResponseEntity<List<News>> getLatestNewsByCategory(
            @Parameter(description = "News category to filter by", required = true, example = "technology")
            @PathVariable String category,
            @Parameter(description = "Maximum number of articles to return (default: 20)", example = "20")
            @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "Target language for translation (default: en)", example = "en")
            @RequestParam(defaultValue = "en") String targetLanguage) {
        log.debug("Received request to get latest {} news items for category: {} in language: {}", 
                 limit, category, targetLanguage);
        try {
            List<News> categoryNews = newsService.getLatestNewsByCategory(category, targetLanguage);
            log.debug("Retrieved {} news items for category: {}", categoryNews.size(), category);
            return ResponseEntity.ok(categoryNews);
        } catch (Exception e) {
            log.error("Error getting latest news by category {}: {}", category, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Get all categories", description = "Retrieve all available news categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/viewCategories")
    public ResponseEntity<List<String>> getAllCategories() {
        log.debug("Received request to get all categories");
        try {
            List<String> categories = newsService.getAllCategories();
            log.debug("Retrieved {} categories", categories.size());
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Error getting all categories: {}", e.getMessage(), e);
            throw e;
        }
    }
    @Operation(summary = "Delete all news articles", description = "Delete all news articles from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All news articles deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
    @Operation(summary = "Delete a news article", description = "Delete a specific news article by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "News article deleted successfully"),
            @ApiResponse(responseCode = "404", description = "News article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/deleteNews/{id}")
    public ResponseEntity<Void> deleteNews(
            @Parameter(description = "ID of the news article to delete", required = true)
            @PathVariable Long id) {
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

} 