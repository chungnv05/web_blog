package com.example.demo.repository;

import com.example.demo.entity.Article;
import com.example.demo.entity.Like;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByArticleId(Long articleId);
    List<Like> findByUserId(Long userId);

    boolean existsByUserAndArticle(User user, Article article);
    Like findByUserAndArticle(User user, Article article);
    
}
