package com.example.demo.repository; 

import com.example.demo.entity.Article;
import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleId(Long articleId);
    List<Comment> findByArticle(Article article);
    List<Comment> findByArticleOrderByCreatedAtDesc(Article article);
}
