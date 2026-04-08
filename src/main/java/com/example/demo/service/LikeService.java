package com.example.demo.service;

import com.example.demo.entity.Article;
import com.example.demo.entity.Like;
import com.example.demo.entity.User;
import com.example.demo.repository.LikeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    // Lấy tất cả like
    public List<Like> findAll() {
        return likeRepository.findAll();
    }

    // Tìm like theo ID
    public Optional<Like> findById(Long id) {
        return likeRepository.findById(id);
    }

    // Lưu like
    public Like save(Like like) {
        return likeRepository.save(like);
    }

    // Xóa like theo ID
    public void deleteById(Long id) {
        likeRepository.deleteById(id);
    }

    // Lấy like theo Article ID
    public List<Like> findByArticleId(Long articleId) {
        return likeRepository.findByArticleId(articleId);
    }

    // Lấy like theo User ID
    public List<Like> findByUserId(Long userId) {
        return likeRepository.findByUserId(userId);
    }
    
    
    public Like findByUserAndArticle(User user, Article article) {
        return likeRepository.findByUserAndArticle(user, article);
    }

    // ✅ Kiểm tra user đã like bài viết chưa
    public boolean existsByUserAndArticle(User user, Article article) {
        return likeRepository.existsByUserAndArticle(user, article);
    }

    // ✅ Toggle like: nếu đã like thì xóa, chưa like thì thêm
    public void toggleLike(User user, Article article) {
        Like like = likeRepository.findByUserAndArticle(user, article);
        if (like != null) {
            likeRepository.delete(like);
        } else {
            likeRepository.save(new Like(user, article));
        }
    }
    
    public void delete(Like like) {
        likeRepository.delete(like);
    }
}
