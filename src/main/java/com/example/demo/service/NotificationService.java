package com.example.demo.service;

import com.example.demo.entity.Article;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(User recipient, User sender, Article article, String type, String message) {
        // Không tạo thông báo nếu người thực hiện hành động chính là chủ sở hữu bài viết
        if (recipient.getId().equals(sender.getId())) {
            return;
        }
        Notification notification = new Notification(recipient, sender, article, type, message);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public long countUnreadNotifications(User user) {
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public void markAllAsRead(User user) {
        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        for (Notification n : notifications) {
            if (!n.isRead()) {
                n.setRead(true);
            }
        }
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void deleteByArticle(Article article) {
        notificationRepository.deleteByArticle(article);
    }

    @Transactional
    public void deleteByUser(User user) {
        notificationRepository.deleteByRecipientOrSender(user, user);
    }
}
