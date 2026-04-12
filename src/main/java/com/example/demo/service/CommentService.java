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
    private final NotificationService notificationService;

    public CommentService(CommentRepository commentRepository, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
    }

    // Lấy tất cả comment
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    // Tìm comment theo ID
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    // Lưu comment và tạo thông báo
    public Comment save(Comment comment) {
        Comment savedComment = commentRepository.save(comment);
        notificationService.createNotification(
            comment.getArticle().getAuthor(),
            comment.getAuthor(),
            comment.getArticle(),
            "COMMENT",
            comment.getAuthor().getName() + " đã bình luận về bài viết của bạn: " + comment.getArticle().getTitle()
        );
        return savedComment;
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
