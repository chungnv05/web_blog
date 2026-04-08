package com.example.demo.service;

import com.example.demo.entity.Article;
import com.example.demo.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }
    
    // lấy tất cả bài viết (không phân trang)
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    // Phân trang tất cả bài viết
    public Page<Article> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    // Tìm theo ID
    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

    // Lưu bài viết
    public Article save(Article article) {
        return articleRepository.save(article);
    }

    // Xóa bài viết
    public void deleteById(Long id) {
        articleRepository.deleteById(id);
    }

    // Tìm bài viết theo tác giả
    public List<Article> findByAuthorId(Long authorId) {
        return articleRepository.findByAuthorId(authorId);
    }

    // Tìm kiếm theo tiêu đề (phân trang)
    public Page<Article> findByTitleContainingIgnoreCase(String keyword, Pageable pageable) {
        return articleRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    // Lọc theo topics
    public List<Article> findByTopicIds(List<Long> topicIds) {
        return articleRepository.findDistinctByTopics_IdIn(topicIds);
    }
    
    // getter / setter
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}
