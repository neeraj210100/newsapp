package org.demo.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.demo.services.CategoryClassificationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CategoryClassificationServiceImpl implements CategoryClassificationService {

    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>();
    private static final String DEFAULT_CATEGORY = "GENERAL";

    static {
        // Technology keywords
        CATEGORY_KEYWORDS.put("TECHNOLOGY", Arrays.asList(
            "technology", "tech", "software", "hardware", "computer", "internet", "digital", 
            "artificial intelligence", "ai", "machine learning", "blockchain", "cryptocurrency", 
            "bitcoin", "startup", "innovation", "app", "mobile", "cloud", "cybersecurity", 
            "data", "programming", "coding", "developer", "silicon valley", "google", "apple", 
            "microsoft", "amazon", "facebook", "meta", "tesla", "spacex", "robot", "automation"
        ));

        // Sports keywords
        CATEGORY_KEYWORDS.put("SPORTS", Arrays.asList(
            "sports", "sport", "football", "soccer", "basketball", "baseball", "tennis", 
            "cricket", "golf", "hockey", "olympics", "fifa", "nfl", "nba", "mlb", "nhl", 
            "championship", "tournament", "match", "game", "player", "team", "coach", 
            "stadium", "league", "athlete", "competition", "victory", "defeat", "score"
        ));

        // Politics keywords
        CATEGORY_KEYWORDS.put("POLITICS", Arrays.asList(
            "politics", "political", "government", "president", "minister", "parliament", 
            "congress", "senate", "election", "vote", "voting", "campaign", "policy", 
            "law", "legislation", "democrat", "republican", "party", "candidate", 
            "white house", "capitol", "supreme court", "justice", "governor", "mayor"
        ));

        // Business keywords
        CATEGORY_KEYWORDS.put("BUSINESS", Arrays.asList(
            "business", "economy", "economic", "finance", "financial", "market", "stock", 
            "investment", "investor", "company", "corporation", "ceo", "revenue", "profit", 
            "earnings", "sales", "trade", "commerce", "industry", "manufacturing", 
            "banking", "wall street", "nasdaq", "dow jones", "merger", "acquisition"
        ));

        // Health keywords
        CATEGORY_KEYWORDS.put("HEALTH", Arrays.asList(
            "health", "medical", "medicine", "doctor", "hospital", "patient", "disease", 
            "virus", "vaccine", "treatment", "therapy", "drug", "pharmaceutical", 
            "covid", "pandemic", "epidemic", "healthcare", "wellness", "fitness", 
            "nutrition", "diet", "mental health", "surgery", "research", "clinical"
        ));

        // Entertainment keywords
        CATEGORY_KEYWORDS.put("ENTERTAINMENT", Arrays.asList(
            "entertainment", "movie", "film", "cinema", "actor", "actress", "director", 
            "music", "singer", "musician", "album", "concert", "celebrity", "hollywood", 
            "bollywood", "tv", "television", "show", "series", "netflix", "streaming", 
            "gaming", "video game", "oscar", "grammy", "award", "festival"
        ));

        // Science keywords
        CATEGORY_KEYWORDS.put("SCIENCE", Arrays.asList(
            "science", "scientific", "research", "study", "discovery", "experiment", 
            "climate", "environment", "space", "nasa", "astronomy", "physics", "chemistry", 
            "biology", "genetics", "dna", "evolution", "laboratory", "scientist", 
            "university", "academic", "journal", "publication", "breakthrough"
        ));

        // World News keywords
        CATEGORY_KEYWORDS.put("WORLD", Arrays.asList(
            "international", "global", "world", "country", "nation", "foreign", 
            "diplomatic", "embassy", "united nations", "un", "war", "conflict", 
            "peace", "treaty", "alliance", "border", "immigration", "refugee", 
            "crisis", "disaster", "earthquake", "flood", "hurricane", "terrorism"
        ));
    }

    @Override
    public String classifyNewsCategory(String title, String description, String content) {
        log.debug("Classifying news category for title: {}", title);
        
        try {
            // Combine all text content for analysis
            String combinedText = combineText(title, description, content).toLowerCase();
            
            // Count keyword matches for each category
            Map<String, Integer> categoryScores = new HashMap<>();
            
            for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
                String category = entry.getKey();
                List<String> keywords = entry.getValue();
                
                int score = calculateCategoryScore(combinedText, keywords);
                if (score > 0) {
                    categoryScores.put(category, score);
                }
            }
            
            // Find the category with the highest score
            String bestCategory = categoryScores.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(DEFAULT_CATEGORY);
            
            log.debug("Classified news as category: {} with scores: {}", bestCategory, categoryScores);
            return bestCategory;
            
        } catch (Exception e) {
            log.error("Error classifying news category: {}", e.getMessage(), e);
            return DEFAULT_CATEGORY;
        }
    }

    private String combineText(String title, String description, String content) {
        StringBuilder combined = new StringBuilder();
        
        if (title != null && !title.trim().isEmpty()) {
            combined.append(title.trim()).append(" ");
        }
        if (description != null && !description.trim().isEmpty()) {
            combined.append(description.trim()).append(" ");
        }
        if (content != null && !content.trim().isEmpty()) {
            combined.append(content.trim());
        }
        
        return combined.toString();
    }

    private int calculateCategoryScore(String text, List<String> keywords) {
        int score = 0;
        
        for (String keyword : keywords) {
            // Use word boundary regex to match whole words only
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword.toLowerCase()) + "\\b");
            long matches = pattern.matcher(text).results().count();
            
            // Give higher weight to longer, more specific keywords
            int keywordWeight = keyword.split("\\s+").length;
            score += matches * keywordWeight;
        }
        
        return score;
    }
} 