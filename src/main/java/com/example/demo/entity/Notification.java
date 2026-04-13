package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    private String type; // LIKE, COMMENT, FOLLOW
    private String message;
    private boolean isRead = false;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(User recipient, User sender, Article article, String type, String message) {
        this.recipient = recipient;
        this.sender = sender;
        this.article = article;
        this.type = type;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
