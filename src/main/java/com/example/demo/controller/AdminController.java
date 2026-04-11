
package com.example.demo.controller;

import com.example.demo.entity.Article;
import com.example.demo.entity.User;
import com.example.demo.entity.Role;
import com.example.demo.entity.Topic;
import com.example.demo.service.TopicService;
import com.example.demo.service.UserService;
import com.example.demo.service.ArticleService;
import com.example.demo.service.ReportService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final TopicService topicService;
    private final ArticleService articleService;
    private final UserService userService;
    private final ReportService reportService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(TopicService topicService,
                           ArticleService articleService,
                           UserService userService,
                           ReportService reportService,
                           PasswordEncoder passwordEncoder) {
        this.topicService = topicService;
        this.articleService = articleService;
        this.userService = userService;
        this.reportService = reportService;
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
        Article article = articleService.findById(id).orElse(null);
        if (article != null) {
            // Xóa tất cả report liên quan đến bài viết
            reportService.deleteByArticle(article);
        }
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
        User user = userService.findById(id).orElse(null);
        if (user != null) {
            // Xóa tất cả bài viết của user
            if (user.getArticles() != null && !user.getArticles().isEmpty()) {
                for (Article article : user.getArticles()) {
                    // Xóa tất cả report liên quan đến bài viết
                    reportService.deleteByArticle(article);
                    articleService.deleteById(article.getId());
                }
            }
        }
        // Xóa user
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    // ------------------- Warn User -------------------
    @PostMapping("/users/{id}/warn")
    public String warnUser(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        User user = userService.findById(id).orElse(null);
        if (user != null) {
            // Trừ 5 điểm
            user.setMark(user.getMark() - 5);

            // Kiểm tra nếu điểm < -10 thì xóa user
            if (user.getMark() < -10) {
                // Xóa tất cả bài viết của user
                if (user.getArticles() != null && !user.getArticles().isEmpty()) {
                    for (Article article : user.getArticles()) {
                        // Xóa tất cả report liên quan đến bài viết
                        reportService.deleteByArticle(article);
                        articleService.deleteById(article.getId());
                    }
                }

                String deletedUserName = user.getUsername();
                userService.deleteById(id);

                // Thêm thông báo
                redirectAttributes.addFlashAttribute("message", "Người dùng " + deletedUserName + " đã bị xóa vì vi phạm 3 lần");
            } else {
                // Nếu còn, chỉ cập nhật điểm
                userService.save(user);
                redirectAttributes.addFlashAttribute("message", "Đã cảnh cáo người dùng " + user.getUsername() + ". Điểm còn lại: " + user.getMark());
            }
        }

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

    // ------------------- Report -------------------
    @GetMapping("/reports")
    public String listReports(Model model) {
        model.addAttribute("reports", reportService.findAll());
        return "admin/reports";
    }

    @PostMapping("/reports/{id}/delete")
    public String deleteReport(@PathVariable Integer id) {
        reportService.deleteById(id);
        return "redirect:/admin/reports";
    }
}

