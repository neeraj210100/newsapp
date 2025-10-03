package org.demo.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Data Transfer Object for News articles")
public class NewsDTO {
    @Schema(description = "Title of the news article", example = "Breaking News: Technology Update")
    private String title;
    @Schema(description = "Brief description of the news article", example = "This article covers the latest technology updates...")
    private String description;
    @Schema(description = "Full content of the news article")
    private String content;
    @Schema(description = "Author of the news article", example = "John Doe")
    private String author;
    @Schema(description = "Original source URL of the news article", example = "https://example.com/news/article")
    private String sourceUrl;
    @Schema(description = "URL of the article's featured image", example = "https://example.com/images/article.jpg")
    private String imageUrl;
    @Schema(description = "Date and time when the article was originally published", example = "2025-10-03T10:30:00")
    private LocalDateTime publishedAt;
    @Schema(description = "Category of the news article", example = "technology", allowableValues = {"technology", "sports", "politics", "entertainment", "business", "health", "science"})
    private String category;
} 