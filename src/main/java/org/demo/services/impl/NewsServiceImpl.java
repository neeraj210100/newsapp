package org.demo.services.impl;

import lombok.RequiredArgsConstructor;
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
        News news = new News();
        news.setTitle(newsDTO.getTitle());
        news.setDescription(newsDTO.getDescription());
        news.setContent(newsDTO.getContent());
        news.setAuthor(newsDTO.getAuthor());
        news.setSourceUrl(newsDTO.getSourceUrl());
        news.setImageUrl(newsDTO.getImageUrl());
        news.setPublishedAt(newsDTO.getPublishedAt() != null ? newsDTO.getPublishedAt() : LocalDateTime.now());
        
        return newsRepository.save(news);
    }

    @Override
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    @Override
    public List<News> searchNews(String keyword) {
        return newsRepository.searchNews(keyword);
    }

    @Override
    public List<News> getDailyNews() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        return newsRepository.findByPublishedAtBetweenOrderByPublishedAtDesc(startOfDay, endOfDay);
    }

    @Override
    public List<News> fetchNewsFromExternalApi(String query) {
        String url = baseUrl + "&apiKey=" + apiKey + "&q=" + query;
        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(NewsApiResponse.class)
                .map(response -> response.getResults().stream()
                        .map(this::mapToNews)
                        .toList())
                .block();
    }

    private News mapToNews(NewsApiResponse.NewsResult result) {
        News news = new News();
        news.setTitle(result.getTitle());
        news.setDescription(result.getDescription());
        news.setContent(result.getContent());
        news.setAuthor(result.getCreator() != null ? String.join(", ", result.getCreator()) : null);
        news.setSourceUrl(result.getLink());
        news.setImageUrl(result.getImageUrl());
        news.setPublishedAt(LocalDateTime.parse(result.getPubDate().replace(" ", "T")));
        return news;
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