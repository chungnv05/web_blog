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
    private final NotificationService notificationService;

    public LikeService(LikeRepository likeRepository, NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.notificationService = notificationService;
    }

    // Lấy tất cả like
    public List<Like> findAll() {
        return likeRepository.findAll();
    }

    // Tìm like theo ID
    public Optional<Like> findById(Long id) {
        return likeRepository.findById(id);
    }

    // Lưu like và tạo thông báo
    public Like save(Like like) {
        Like savedLike = likeRepository.save(like);
        notificationService.createNotification(
            like.getArticle().getAuthor(),
            like.getUser(),
            like.getArticle(),
            "LIKE",
            like.getUser().getName() + " đã thích bài viết của bạn: " + like.getArticle().getTitle()
        );
        return savedLike;
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
            notificationService.createNotification(
                article.getAuthor(),
                user,
                article,
                "LIKE",
                user.getName() + " đã thích bài viết của bạn: " + article.getTitle()
            );
        }
    }
    
    public void delete(Like like) {
        likeRepository.delete(like);
    }
}
