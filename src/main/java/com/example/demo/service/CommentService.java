package com.example.demo.service;

import com.example.demo.entity.Article;
import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // Lấy tất cả comment
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    // Tìm comment theo ID
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    // Lưu comment
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    // Xóa comment theo ID
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    // Lấy comment theo Article ID
    public List<Comment> findByArticleId(Long articleId) {
        return commentRepository.findByArticleId(articleId);
    }

    // Thêm hàm lấy comment theo entity Article
    public List<Comment> findByArticle(Article article) {
        return commentRepository.findByArticle(article);
    }
    
    public List<Comment> findByArticleOrderByCreatedAtDesc(Article article) {
        return commentRepository.findByArticleOrderByCreatedAtDesc(article);
    }
}
