package com.example.demo.service.history;

import com.example.demo.entity.Quiz;
import com.example.demo.entity.QuizHistory;
import com.example.demo.repository.history.QuizHistoryRepository;
import com.example.demo.repository.history.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.example.demo.entity.User;

@Service
public class QuizHistoryService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizHistoryRepository quizHistoryRepository;

    public void saveQuizHistory(Long quizId, int score, int total, User user) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz != null) {
            QuizHistory history = new QuizHistory();
            history.setUser(user);
            history.setQuiz(quiz);
            history.setScore(score);
            history.setTotalQuestions(total);
            history.setCompletedAt(LocalDateTime.now());
            quizHistoryRepository.save(history);
        }
    }
    public List<QuizHistory> findByUserOrderByCompletedAtDesc(User user) {
        return quizHistoryRepository.findByUserOrderByCompletedAtDesc(user);
    }

    public void deleteByQuiz(Quiz quiz) {
        quizHistoryRepository.deleteByQuiz(quiz);
    }
}