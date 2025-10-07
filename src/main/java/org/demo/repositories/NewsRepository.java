package org.demo.repositories;

import org.demo.models.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {


    @Query("SELECT n FROM News n WHERE " +
    "n.publishedAt BETWEEN :start AND :end ORDER BY n.publishedAt DESC")
    List<News> findLatestNewsOrderByPublishedAtDesc(
        LocalDateTime start, 
        LocalDateTime end
    );
    
    @Query("SELECT n FROM News n WHERE " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(n.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY n.publishedAt DESC")
    List<News> searchNews(@Param("keyword") String keyword);
    
    @Query("SELECT DISTINCT n.category FROM News n WHERE n.category IS NOT NULL ORDER BY n.category")
    List<String> findAllDistinctCategories();

    int deleteByCreatedAtBefore(LocalDateTime date);
    Optional<News> findByTitleAndDescription(String title, String sourceUrl);
    boolean existsByTitleAndDescription(String title, String sourceUrl);
} 