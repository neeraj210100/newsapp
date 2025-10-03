package org.demo.services;

import org.demo.models.News;
import org.demo.models.dto.NewsDTO;

import java.util.List;

public interface NewsService {
    News createNews(NewsDTO newsDTO);
    void deleteNews(Long id);
    void deleteAllNews();
    List<News> searchNews(String keyword);
    List<News> getDailyNews(String targetLanguage);
    List<News> fetchNewsFromExternalApi(String query, String targetLanguage);
    List<News> getLatestNewsByCategory(String category, String targetLanguage);

    List<News> getLatestNewsByCategory(String category, int limit, String targetLanguage);

    List<String> getAllCategories();
}