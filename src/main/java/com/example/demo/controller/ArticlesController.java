package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import com.example.demo.service.history.ReadingHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ArticlesController {

    private final ReadingHistoryService readingHistoryService;
    private ArticleService articleService;
    private UserService userService;
    private CommentService commentService;
    private LikeService likeService;
    private TopicService topicService;

    public ArticlesController(ArticleService articleService, UserService userService, CommentService commentService,
                              LikeService likeService, TopicService topicService, ReadingHistoryService readingHistoryService) {
        this.articleService = articleService;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.topicService = topicService;
        this.readingHistoryService = readingHistoryService;
    }

    // Hiển thị form tạo bài viết
    @GetMapping("/articles/new")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        List<Topic> topics = topicService.findAll();
        model.addAttribute("topics", topics);
        return "articles/create"; // render file articles/create.html
    }

    // Xử lý submit form tạo bài viết
    @PostMapping("/articles")
    public String createArticle(@ModelAttribute("article") Article article,
            HttpSession session,
            @RequestParam(value = "topics", required = false) List<Long> topicIds) {
        // Lấy user hiện tại từ session
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login"; // chưa đăng nhập thì về login
        }

        // Gán thông tin cho bài viết
        article.setAuthor(currentUser);
        article.setCreatedAt(LocalDateTime.now());

        // Gán chủ đề
        if (topicIds != null) {
            List<Topic> selectedTopics = topicService.findByIds(topicIds);
            article.setTopics(selectedTopics);
        }
        if (article.getQuiz() != null) {
            Quiz quiz = article.getQuiz();

            quiz.setArticle(article); 

            if (quiz.getQuestions() != null) {
                for (Question q : quiz.getQuestions()) {
                    q.setQuiz(quiz); 
                }
            }
        }
        // Lưu bài viết
        articleService.save(article);

        return "redirect:/"; // sau khi tạo xong quay về trang chủ
    }

    // 📝 Chi tiết bài viết
    @GetMapping("/articles/{id}")
    public String articleDetail(@PathVariable Long id,
            HttpSession session,
            Model model) {
        // Lấy bài viết
        Article article = articleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với id: " + id));

        // Lấy danh sách bình luận theo thời gian giảm dần
        List<Comment> comments = commentService.findByArticleOrderByCreatedAtDesc(article);

        // Kiểm tra người dùng hiện tại đã thích bài viết chưa
        boolean userLikedArticle = false;
        boolean isAuthenticated = false;

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            isAuthenticated = true;
            userLikedArticle = likeService.existsByUserAndArticle(currentUser, article);
            readingHistoryService.saveHistory(currentUser, article);
        }

        // Truyền dữ liệu cho template
        model.addAttribute("article", article);
        model.addAttribute("comments", comments);
        model.addAttribute("userLikedArticle", userLikedArticle);
        model.addAttribute("isAuthenticated", isAuthenticated);

        return "articles/article"; // render file articles/article.html
    }

    // 💬 Tạo comment
    @PostMapping("/articles/{id}/comments")
    public String createComment(@PathVariable Long id,
            @RequestParam("content") String content,
            HttpSession session) {
        // Lấy user hiện tại từ session
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login"; // chưa đăng nhập thì về login
        }

        // Lấy bài viết
        Article article = articleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với id: " + id));

        // Tạo comment mới
        Comment comment = new Comment();
        comment.setAuthor(currentUser);
        comment.setArticle(article);
        comment.setContent(content);

        // Lưu comment
        commentService.save(comment);

        // Quay lại trang chi tiết bài viết
        return "redirect:/articles/" + id;
    }

    // ❤️ Like bài viết
    @PostMapping("/articles/{id}/like")
    public String likeArticle(@PathVariable Long id, HttpSession session) {
        // Lấy user hiện tại từ session
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login"; // chưa đăng nhập thì về login
        }

        // Lấy bài viết
        Article article = articleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với id: " + id));

        // Kiểm tra xem user đã like chưa
        Like existingLike = likeService.findByUserAndArticle(currentUser, article);

        if (existingLike != null) {
            // Nếu đã like, xóa like đi
            likeService.delete(existingLike);

            // Giảm điểm cho tác giả bài viết
            User author = article.getAuthor();
            author.setMark(author.getMark() - 1);
            userService.save(author);
        } else {
            // Nếu chưa like, tạo like mới
            Like newLike = new Like();
            newLike.setUser(currentUser);
            newLike.setArticle(article);
            likeService.save(newLike);

            // Tăng điểm cho tác giả bài viết
            User author = article.getAuthor();
            author.setMark(author.getMark() + 1);
            userService.save(author);
            // lưu lại user author (nếu có UserService thì gọi save)
        }

        // Quay lại trang chi tiết bài viết
        return "redirect:/articles/" + id;
    }

}
