package com.example.demo.controller;

import com.example.demo.service.history.QuizHistoryService;
import org.springframework.ui.Model;
import com.example.demo.entity.QuizHistory;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.entity.User;

import java.util.List;

@Controller
public class QuizController {
    @Autowired
    private QuizHistoryService quizHistoryService;

    @PostMapping("/quiz/{id}/submit")
    public String submitQuiz(@PathVariable Long id,
                             @RequestParam("score") int score,
                             @RequestParam("total") int total,
                             HttpSession session,
                             Model model) {

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            quizHistoryService.saveQuizHistory(id, score,total, currentUser);
            return "success";
        }
        return "error";
    }
}
