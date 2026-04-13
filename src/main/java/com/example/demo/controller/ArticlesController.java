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
@RequestMapping("/articles")
public class ArticlesController {

    private final ReadingHistoryService readingHistoryService;
    private ArticleService articleService;
    private UserService userService;
    private CommentService commentService;
    private LikeService likeService;
    private TopicService topicService;
    private SeriesService seriesService;
    private ReportService reportService;

    public ArticlesController(ArticleService articleService,
                              UserService userService,
                              CommentService commentService,
                              LikeService likeService,
                              TopicService topicService,
                              SeriesService seriesService,
                              ReportService reportService,
                              ReadingHistoryService readingHistoryService) {

        this.articleService = articleService;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.topicService = topicService;
        this.seriesService = seriesService;
        this.reportService = reportService;
        this.readingHistoryService = readingHistoryService;
    }

    // Hiển thị form tạo bài viết
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        List<Topic> topics = topicService.findAll();
        model.addAttribute("topics", topics);
        return "articles/create"; // render file articles/create.html
    }

    // Xử lý submit form tạo bài viết
    @PostMapping("/new")
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

    // Chi tiết bài viết
    @GetMapping("/{id}")
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
        boolean isOwner = false;
        boolean isAdmin = false;

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            isAuthenticated = true;
            userLikedArticle = likeService.existsByUserAndArticle(currentUser, article);

            // Kiểm tra quyền
            isOwner = currentUser.getId().equals(article.getAuthor().getId());
            isAdmin = currentUser.getRole() == Role.ADMIN;

            // Lưu lịch sử đọc
            readingHistoryService.saveHistory(currentUser, article);
        }

        // Truyền dữ liệu cho template
        model.addAttribute("article", article);
        model.addAttribute("comments", comments);
        model.addAttribute("userLikedArticle", userLikedArticle);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isAdmin", isAdmin);

        return "articles/article"; // render file articles/article.html
    }

    // 💬 Tạo comment
    @PostMapping("/{id}/comments")
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

    //  Like bài viết
    @PostMapping("{id}/like")
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

    // Báo cáo bài viết
    @PostMapping("/{id}/report")
    public String reportArticle(@PathVariable Long id,
                                @RequestParam(value = "reportReason", required = false) String reportReason,
                                @RequestParam(value = "content", required = false) String content,
                                HttpSession session) {
        // Lấy user hiện tại từ session
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Lấy bài viết
        Article article = articleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với id: " + id));

        // Tạo report mới
        Report report = new Report();
        report.setUser(currentUser);
        report.setArticle(article);

        // Kết hợp lý do báo cáo và chi tiết thêm
        StringBuilder reportContent = new StringBuilder();
        if (reportReason != null && !reportReason.isEmpty()) {
            reportContent.append("Lý do: ").append(reportReason);
        }
        if (content != null && !content.isEmpty()) {
            if (reportContent.length() > 0) {
                reportContent.append("\n\n");
            }
            reportContent.append("Chi tiết: ").append(content);
        }

        report.setContent(reportContent.toString());
        report.setCreatedAt(LocalDateTime.now());

        // Lưu report
        reportService.save(report);

        // Quay lại trang chi tiết bài viết
        return "redirect:/articles/" + id;
    }

    @GetMapping("/add-article")
    public String showAddArticleForm(@RequestParam Long articleId,
                                     HttpSession session,
                                     Model model) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Lấy bài viết từ DB
        Article article = articleService.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        // Kiểm tra xem user có quyền add article vào series không (chỉ chủ sở hữu bài viết)
        if (!article.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/articles/" + articleId;
        }

        // Lấy tất cả series của user
        List<Series> seriesList = seriesService.getAllByUser(currentUser.getId());

        model.addAttribute("article", article);
        model.addAttribute("seriesList", seriesList);

        // render file templates/series/add_article_to_series.html
        return "articles/add_article_to_series";
    }

    @PostMapping("/add-to-series")
    public String addArticleToSeries(@RequestParam Long articleId,
                                     @RequestParam Long seriesId,
                                     HttpSession session) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Lấy bài viết từ DB
        Article article = articleService.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        // Kiểm tra xem user có quyền add article vào series không
        if (!article.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/articles/" + articleId;
        }

        // Lấy series từ DB
        Series series = seriesService.findById(seriesId)
                .orElseThrow(() -> new RuntimeException("Series not found"));

        // Kiểm tra xem user có quyền add vào series này không
        if (!series.getOwner().getId().equals(currentUser.getId())) {
            return "redirect:/articles/" + articleId;
        }

        // Thêm bài viết vào series
        if (!series.getArticles().contains(article)) {
            series.getArticles().add(article);
            seriesService.save(series);
        }

        // Quay lại trang chi tiết bài viết
        return "redirect:/articles/" + articleId;
    }

}
