package org.demo.repositories;

import org.demo.models.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    List<News> findByTitleContainingIgnoreCase(String keyword);
    
    List<News> findByPublishedAtBetweenOrderByPublishedAtDesc(
        LocalDateTime start, 
        LocalDateTime end
    );
    
    @Query("SELECT n FROM News n WHERE " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(n.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<News> searchNews(@Param("keyword") String keyword);
} 