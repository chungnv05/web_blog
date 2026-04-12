package com.example.demo.controller;

import com.example.demo.entity.ReadingHistory;
import com.example.demo.entity.QuizHistory; // Thêm import này
import com.example.demo.entity.User;
import com.example.demo.service.history.ReadingHistoryService;
import com.example.demo.service.history.QuizHistoryService; // Thêm import này
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HistoryController {

    @Autowired
    private ReadingHistoryService readingHistoryService;
    @Autowired
    private QuizHistoryService quizHistoryService;

    @GetMapping("/history")
    public String showHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        List<ReadingHistory> historyList = readingHistoryService.findByUserOrderByViewedAtDesc(user);
        List<QuizHistory> quizHistoryList = quizHistoryService.findByUserOrderByCompletedAtDesc(user);

        model.addAttribute("historyList", historyList);
        model.addAttribute("quizHistoryList", quizHistoryList);

        return "users/history";
    }
}