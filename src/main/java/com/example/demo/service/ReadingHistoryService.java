package com.example.demo.service;

import com.example.demo.entity.Article;
import com.example.demo.entity.ReadingHistory;
import com.example.demo.entity.User;
import com.example.demo.repository.ReadingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReadingHistoryService {
    @Autowired
    private ReadingHistoryRepository historyRepository;

    public void saveHistory(User user, Article article) {
        ReadingHistory history = historyRepository.findByUserAndArticle(user, article)
                .orElse(new ReadingHistory(user, article, LocalDateTime.now()));
        history.setViewedAt(LocalDateTime.now());
        historyRepository.save(history);
    }
    public List<ReadingHistory> findByUserOrderByViewedAtDesc(User user) {
        return historyRepository.findByUserOrderByViewedAtDesc(user);
    }
}


