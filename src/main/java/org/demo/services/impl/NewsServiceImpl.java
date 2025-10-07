package org.demo.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.models.News;
import org.demo.models.dto.NewsDTO;
import org.demo.repositories.NewsRepository;
import org.demo.services.CategoryClassificationService;
import org.demo.services.NewsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final WebClient.Builder webClientBuilder;
    private final TranslationService translationService;
    private final CategoryClassificationService categoryClassificationService;
    private final WebScraperService webScraperService;
    
    @Value("${news.api.key}")
    private String apiKey;

    @Value("${news.api.base-url}")
    private String baseUrl;


    @Override
    public List<News> searchNews(String keyword) {
        log.debug("Searching news with keyword: {}", keyword);
        try {
            List<News> results = newsRepository.searchNews(keyword);
            log.debug("Found {} news items matching keyword: {}", results.size(), keyword);
            return results;
        } catch (Exception e) {
            log.error("Error searching news with keyword {}: {}", keyword, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<News> getDailyNews(String targetLanguage) {
        log.debug("Getting daily news with translation to language: {}", targetLanguage);
        try {
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now();
            log.debug("start: {} end : {}", startOfDay,endOfDay);
            List<News> dailyNews = newsRepository.findLatestNewsOrderByPublishedAtDesc(startOfDay, endOfDay);

            // Translate if target language is provided
            if (targetLanguage != null && !targetLanguage.isEmpty()) {
                return translateNewsList(dailyNews, targetLanguage);
            }

            return dailyNews;
        } catch (Exception e) {
            log.error("Error getting daily news: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public List<News> fetchNewsFromExternalApi(String query, String targetLanguage) {
        log.debug("Fetching news from external API with query: {}", query);
        try {
            String url = baseUrl + "&apiKey=" + apiKey + "&q=" + query;
            log.debug("Making request to external API: {}", url.replace(apiKey, "API_KEY_HIDDEN"));
            
            List<News> fetchedNews = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(NewsApiResponse.class)
                    .map(response -> {
                        log.debug("Received {} results from external API", response.getResults().size());
                        List<NewsDTO> newsDTOs = response.getResults().stream()
                                .map(this::mapToNewsDTO)
                                .toList();
                        
                        // Fetch content and images sequentially from source URLs
                        return enrichNewsSequentially(newsDTOs);
                    })
                    .block();
            if(Objects.isNull(fetchedNews)) return new ArrayList<>();
            // Translate the fetched news if target language is provided
            if (targetLanguage != null && !targetLanguage.isEmpty() && !fetchedNews.isEmpty()) {
                return translateNewsList(fetchedNews, targetLanguage);
            }
            
            return fetchedNews;
        } catch (Exception e) {
            log.error("Error fetching/saving news from external API with query {}: {}", query, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Enriches news DTOs sequentially by fetching full content and images from source URLs
     */
    private List<News> enrichNewsSequentially(List<NewsDTO> newsDTOs) {
        log.info("Starting sequential enrichment of {} news articles", newsDTOs.size());
        List<News> enrichedNews = new ArrayList<>();
        
        for (int i = 0; i < newsDTOs.size(); i++) {
            NewsDTO newsDTO = newsDTOs.get(i);
            log.debug("Processing article {}/{}: {}", i + 1, newsDTOs.size(), newsDTO.getTitle());
            
            try {
                // Enrich the news DTO with scraped content and images
                enrichNewsDTO(newsDTO);
                
                // Create and save the news
                News savedNews = createNews(newsDTO);
                if (savedNews != null) {
                    enrichedNews.add(savedNews);
                    log.debug("Successfully enriched and saved article {}/{}", i + 1, newsDTOs.size());
                }
                
                // Add a small delay between requests to be respectful to source servers
                if (i < newsDTOs.size() - 1) {
                    Thread.sleep(500); // 500ms delay between requests
                }
            } catch (InterruptedException e) {
                log.warn("Thread interrupted during sequential processing");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error enriching news item {}: {}", newsDTO.getTitle(), e.getMessage());
                // Continue with next article even if one fails
            }
        }
        
        log.info("Completed sequential enrichment. Successfully processed {}/{} articles", 
                enrichedNews.size(), newsDTOs.size());
        return enrichedNews;
    }

    /**
     * Enriches a single NewsDTO by fetching content and images from source URL
     */
    private void enrichNewsDTO(NewsDTO newsDTO) {
        if (newsDTO.getSourceUrl() == null || newsDTO.getSourceUrl().isEmpty()) {
            log.debug("No source URL available for article: {}", newsDTO.getTitle());
            return;
        }

        try {
            // Fetch full content if not already present or too short
            if (newsDTO.getContent() == null || newsDTO.getContent().length() < 200) {
                log.debug("Fetching full content from: {}", newsDTO.getSourceUrl());
                String scrapedContent = webScraperService.fetchArticleContent(newsDTO.getSourceUrl());
                
                if (scrapedContent != null && !scrapedContent.isEmpty()) {
                    newsDTO.setContent(scrapedContent);
                    log.debug("Successfully fetched {} characters of content", scrapedContent.length());
                } else {
                    log.debug("Could not fetch content, keeping original");
                }
            }

            // Fetch image if not already present
            if (newsDTO.getImageUrl() == null || newsDTO.getImageUrl().isEmpty()) {
                log.debug("Fetching image from: {}", newsDTO.getSourceUrl());
                String scrapedImage = webScraperService.fetchBestImage(newsDTO.getSourceUrl());
                
                if (scrapedImage != null && !scrapedImage.isEmpty()) {
                    newsDTO.setImageUrl(scrapedImage);
                    log.debug("Successfully fetched image: {}", scrapedImage);
                } else {
                    log.debug("Could not fetch image from source");
                }
            }
        } catch (Exception e) {
            log.error("Error enriching news DTO from {}: {}", newsDTO.getSourceUrl(), e.getMessage());
            // Continue with original data if enrichment fails
        }
    }

    @Override
    public News createNews(NewsDTO newsDTO) {
        log.debug("Creating news with title: {}", newsDTO.getTitle());
        try {
            // Check for existing news with same title and source URL
            if (newsRepository.existsByTitleAndDescription(newsDTO.getTitle(), newsDTO.getDescription())) {
                log.debug("News article already exists with title: {} and desc: {}",
                        newsDTO.getTitle(), newsDTO.getDescription());
                return newsRepository.findByTitleAndDescription(newsDTO.getTitle(), newsDTO.getDescription())
                        .orElseThrow(() -> new RuntimeException("News article not found after existence check"));
            }

            News news = new News();
            news.setTitle(newsDTO.getTitle());
            news.setDescription(newsDTO.getDescription());
            news.setContent(newsDTO.getContent());
            news.setAuthor(newsDTO.getAuthor());
            news.setSourceUrl(newsDTO.getSourceUrl());
            news.setImageUrl(newsDTO.getImageUrl());
            news.setPublishedAt(newsDTO.getPublishedAt() != null ? newsDTO.getPublishedAt() : LocalDateTime.now());

            // Automatically classify category if not provided
            if (newsDTO.getCategory() == null || newsDTO.getCategory().trim().isEmpty()) {
                String category = categoryClassificationService.classifyNewsCategory(
                        newsDTO.getTitle(), newsDTO.getDescription(), newsDTO.getContent());
                news.setCategory(category);
                log.debug("Auto-classified news category as: {}", category);
            } else {
                news.setCategory(newsDTO.getCategory().toUpperCase());
            }

            News savedNews = newsRepository.save(news);
            log.debug("Successfully created news with ID: {} and category: {}", savedNews.getId(), savedNews.getCategory());
            return savedNews;
        } catch (Exception e) {
            log.error("Error creating news: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public List<News> getLatestNewsByCategory(String category, String targetLanguage) {
        log.debug("Getting latest {} news items for category: {} in language: {}", category, targetLanguage);
        try {
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now();
            log.debug("start: {} end : {}", startOfDay,endOfDay);
            List<News> dailyNews = newsRepository.findLatestNewsOrderByPublishedAtDesc(startOfDay, endOfDay);

            List<News> categoryNews = dailyNews
                    .stream()
                    .filter(e-> e.getCategory().equalsIgnoreCase(category))
                    .toList();
            
            log.debug("Found {} news items for category: {}", categoryNews.size(), category);
            
            // Translate if target language is provided
            if (targetLanguage != null && !targetLanguage.isEmpty() && !targetLanguage.equalsIgnoreCase("en")) {
                return translateNewsList(categoryNews, targetLanguage);
            }
            
            return categoryNews;
        } catch (Exception e) {
            log.error("Error getting latest news by category {}: {}", category, e.getMessage(), e);
            throw e;
        }
    }


    private List<News> translateNewsList(List<News> newsList, String targetLanguage) {
        return newsList.stream()
                .map(news -> {
                    try {
                        News translatedNews = new News();
                        translatedNews.setId(news.getId());
                        translatedNews.setTitle(translationService.translateText(news.getTitle(), targetLanguage));
                        translatedNews.setDescription(translationService.translateText(news.getDescription(), targetLanguage));
                        translatedNews.setContent(translationService.translateText(news.getContent(), targetLanguage));
                        translatedNews.setAuthor(news.getAuthor());
                        translatedNews.setSourceUrl(news.getSourceUrl());
                        translatedNews.setImageUrl(news.getImageUrl());
                        translatedNews.setCategory(news.getCategory());
                        translatedNews.setPublishedAt(news.getPublishedAt());
                        return translatedNews;
                    } catch (Exception e) {
                        log.error("Error translating news item {}: {}", news.getId(), e.getMessage());
                        return news; // Return original if translation fails
                    }
                })
                .toList();
    }

    private NewsDTO mapToNewsDTO(NewsApiResponse.NewsResult result) {
        log.trace("Mapping external API result to NewsDTO: {}", result.getTitle());
        try {
            String title = result.getTitle() != null ? result.getTitle().trim() : "";
            String description = result.getDescription() != null ? result.getDescription().trim() : "";
            String content = result.getContent() != null ? result.getContent().trim() : "";
            
            NewsDTO newsDTO = new NewsDTO();
            newsDTO.setTitle(title);
            newsDTO.setDescription(description);
            newsDTO.setContent(content);
            newsDTO.setAuthor(result.getCreator() != null ? String.join(", ", result.getCreator()) : null);
            newsDTO.setSourceUrl(result.getLink());
            newsDTO.setImageUrl(result.getImageUrl());
            newsDTO.setPublishedAt(LocalDateTime.parse(result.getPubDate().replace(" ", "T")));
            return newsDTO;
        } catch (Exception e) {
            log.error("Error mapping news item with title {} to DTO: {}", result.getTitle(), e.getMessage(), e);
            throw e;
        }
    }


    private static class NewsApiResponse {
        private List<NewsResult> results;

        public List<NewsResult> getResults() {
            return results;
        }

        public void setResults(List<NewsResult> results) {
            this.results = results;
        }

        private static class NewsResult {
            private String title;
            private String description;
            private String content;
            private String[] creator;
            private String link;
            private String image_url;
            private String pubDate;

            public String getTitle() {
                return title;
            }

            public String getDescription() {
                return description;
            }

            public String getContent() {
                return content;
            }

            public String[] getCreator() {
                return creator;
            }

            public String getLink() {
                return link;
            }

            public String getImageUrl() {
                return image_url;
            }

            public String getPubDate() {
                return pubDate;
            }
        }
    }
    @Override
    public List<String> getAllCategories() {
        log.debug("Getting all distinct categories");
        try {
            List<String> categories = newsRepository.findAllDistinctCategories();
            log.debug("Found {} distinct categories: {}", categories.size(), categories);
            return categories;
        } catch (Exception e) {
            log.error("Error getting all categories: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteNews(Long id) {
        log.debug("Attempting to delete news with ID: {}", id);
        try {
            newsRepository.deleteById(id);
            log.debug("Successfully deleted news with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting news with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    @Override
    public void deleteAllNews() {
        log.debug("Attempting to delete all news from database");
        try {
            newsRepository.deleteAll();
            log.debug("Successfully deleted all news from database");
        } catch (Exception e) {
            log.error("Error deleting all news: {}", e.getMessage(), e);
            throw e;
        }
    }

} 