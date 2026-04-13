package com.example.demo.repository.history;

import com.example.demo.entity.Article;
import com.example.demo.entity.ReadingHistory;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {
    List<ReadingHistory> findByUserOrderByViewedAtDesc(User user);
    Optional<ReadingHistory> findByUserAndArticle(User user, Article article);
}
