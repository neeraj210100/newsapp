package org.demo.models.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NewsDTO {
    private String title;
    private String description;
    private String content;
    private String author;
    private String sourceUrl;
    private String imageUrl;
    private LocalDateTime publishedAt;
    private String category;
} 