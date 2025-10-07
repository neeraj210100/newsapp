package org.demo.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "news", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"title", "source_url"})
})
@Schema(description = "News article entity")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the news article", example = "1")
    private Long id;

    @Column(nullable = false, length = 500)
    @Schema(description = "Title of the news article", example = "Breaking News: Technology Update")
    private String title;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Brief description of the news article", example = "This article covers the latest technology updates...")
    private String description;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Full content of the news article")
    private String content;

    @Schema(description = "Author of the news article", example = "John Doe")
    private String author;

    @Column(name = "source_url", columnDefinition = "TEXT")
    @Schema(description = "Original source URL of the news article", example = "https://example.com/news/article")
    private String sourceUrl;

    @Column(name = "image_url", columnDefinition = "TEXT")
    @Schema(description = "URL of the article's featured image", example = "https://example.com/images/article.jpg")
    private String imageUrl;

    @Column(name = "published_at")
    @Schema(description = "Date and time when the article was originally published", example = "2025-10-03T10:30:00")
    private LocalDateTime publishedAt;

    @Column(name = "created_at")
    @Schema(description = "Date and time when the article was saved to the database", example = "2025-10-03T10:35:00")
    private LocalDateTime createdAt;

    @Column(name = "category", length = 100)
    @Schema(description = "Category of the news article", example = "technology", allowableValues = {"technology", "sports", "politics", "entertainment", "business", "health", "science"})
    private String category;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 