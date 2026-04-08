package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.demo.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByAuthorId(Long authorId);
    Page<Article> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    List<Article> findDistinctByTopics_IdIn(List<Long> topicIds);
}
