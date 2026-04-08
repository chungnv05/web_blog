
package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.Role;
import com.example.demo.entity.Topic;
import com.example.demo.service.TopicService;
import com.example.demo.service.UserService;
import com.example.demo.service.ArticleService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final TopicService topicService;
    private final ArticleService articleService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(TopicService topicService,
                           ArticleService articleService,
                           UserService userService,
                           PasswordEncoder passwordEncoder) {
        this.topicService = topicService;
        this.articleService = articleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @GetMapping
    public String adminHome() {
        return "admin/home"; // render file templates/admin/home.html
    }

    // ------------------- Topic -------------------
    @GetMapping("/topics")
    public String listTopics(Model model) {
        model.addAttribute("topics", topicService.findAll());
        model.addAttribute("newTopic", new Topic());
        return "admin/topics";
    }

    @PostMapping("/topics")
    public String addTopic(@ModelAttribute("newTopic") Topic topic) {
        topicService.save(topic);
        return "redirect:/admin/topics";
    }

    @PostMapping("/topics/{id}/delete")
    public String deleteTopic(@PathVariable Long id) {
        topicService.deleteById(id);
        return "redirect:/admin/topics";
    }

    // ------------------- Article -------------------
    @GetMapping("/articles")
    public String listArticles(Model model) {
        model.addAttribute("articles", articleService.findAll());
        return "admin/articles";
    }

    @PostMapping("/articles/{id}/delete")
    public String deleteArticle(@PathVariable Long id) {
        articleService.deleteById(id);
        return "redirect:/admin/articles";
    }

    // ------------------- User -------------------
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    // ------------------- Admin -------------------
    @GetMapping("/admins")
    public String listAdmins(Model model) {
        model.addAttribute("admins", userService.findByRole(Role.ADMIN));
        model.addAttribute("newAdmin", new User());
        return "admin/admins";
    }

    @PostMapping("/admins")
    public String addAdmin(@ModelAttribute("newAdmin") User admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole(Role.ADMIN);
        userService.save(admin);
        return "redirect:/admin/admins";
    }

    @PostMapping("/admins/{id}/delete")
    public String deleteAdmin(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/admins";
    }
}
