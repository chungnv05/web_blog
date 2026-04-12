package com.example.demo.repository.history;

import com.example.demo.entity.QuizHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.User;

import java.util.List;

@Repository
public interface QuizHistoryRepository extends JpaRepository<QuizHistory, Long> {
    List<QuizHistory> findByUserOrderByCompletedAtDesc(User user);
}