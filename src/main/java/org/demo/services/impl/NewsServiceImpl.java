package org.demo.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.models.News;
import org.demo.models.dto.NewsDTO;
import org.demo.repositories.NewsRepository;
import org.demo.services.NewsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${news.api.key}")
    private String apiKey;

    @Value("${news.api.base-url}")
    private String baseUrl;

    @Override
    public News createNews(NewsDTO newsDTO) {
        log.debug("Creating news with title: {}", newsDTO.getTitle());
        try {
            News news = new News();
            news.setTitle(newsDTO.getTitle());
            news.setDescription(newsDTO.getDescription());
            news.setContent(newsDTO.getContent());
            news.setAuthor(newsDTO.getAuthor());
            news.setSourceUrl(newsDTO.getSourceUrl());
            news.setImageUrl(newsDTO.getImageUrl());
            news.setPublishedAt(newsDTO.getPublishedAt() != null ? newsDTO.getPublishedAt() : LocalDateTime.now());
            
            News savedNews = newsRepository.save(news);
            log.debug("Successfully created news with ID: {}", savedNews.getId());
            return savedNews;
        } catch (Exception e) {
            log.error("Error creating news: {}", e.getMessage(), e);
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
    public List<News> getDailyNews() {
        log.debug("Fetching daily news");
        try {
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            
            List<News> dailyNews = newsRepository.findByPublishedAtBetweenOrderByPublishedAtDesc(startOfDay, endOfDay);
            log.debug("Found {} news items for today between {} and {}", dailyNews.size(), startOfDay, endOfDay);
            return dailyNews;
        } catch (Exception e) {
            log.error("Error fetching daily news: {}", e.getMessage(), e);
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

    @Override
    public List<News> fetchNewsFromExternalApi(String query) {
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
                        return response.getResults().stream()
                                .map(this::mapToNewsDTO)
                                .map(this::createNews)
                                .toList();
                    })
                    .block();
            
            log.debug("Successfully fetched and saved {} news items", fetchedNews.size());
            return fetchedNews;
        } catch (Exception e) {
            log.error("Error fetching/saving news from external API with query {}: {}", query, e.getMessage(), e);
            throw e;
        }
    }

    private NewsDTO mapToNewsDTO(NewsApiResponse.NewsResult result) {
        log.trace("Mapping external API result to NewsDTO: {}", result.getTitle());
        try {
            NewsDTO newsDTO = new NewsDTO();
            newsDTO.setTitle(result.getTitle());
            newsDTO.setDescription(result.getDescription());
            newsDTO.setContent(result.getContent());
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


} 