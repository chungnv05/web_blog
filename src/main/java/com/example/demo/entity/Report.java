package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String content;
    
    private LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    private Article article;
    
    // Constructor mặc định
    public Report() {}
    
    // Constructor có tham số
    public Report(String content, User user, Article article) {
        this.content = content;
        this.user = user;
        this.article = article;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getter & Setter
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Article getArticle() {
        return article;
    }
    
    public void setArticle(Article article) {
        this.article = article;
    }
}
