package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Controller
public class ArticlesController {

    private ArticleService articleService;
    private UserService userService;
    private CommentService commentService;
    private LikeService likeService;
    private TopicService topicService;

    public ArticlesController(ArticleService articleService, UserService userService, CommentService commentService, LikeService likeService, TopicService topicService) {
        this.articleService = articleService;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.topicService = topicService;

    }


    // Hiển thị form tạo bài viết
    @GetMapping("/articles/new")
    public String showCreateForm(HttpSession session, Model model) {
        // nếu chưa đăng nhập, chuyển đến /login
        User sessionUser = (User) session.getAttribute("currentUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        
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

    
        // Hiển thị form chỉnh sửa
    @GetMapping("/articles/{id}/edit")
    public String editArticleForm(@PathVariable Long id, Model model, HttpSession session) {
        Article article = articleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với id: " + id));

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !article.getAuthor().getUsername().equals(currentUser.getUsername())) {
            return "redirect:/articles/" + id; // chỉ author mới được chỉnh sửa
        }

        // Truyền dữ liệu sẵn có cho form
        model.addAttribute("article", article);
        List<Topic> topics = topicService.findAll();
        model.addAttribute("topics", topics);

        return "articles/create"; // dùng lại giao diện create.html
    }

    // Xử lý POST chỉnh sửa
    @PostMapping("/articles/{id}/edit")
    public String updateArticle(@PathVariable Long id,
                                @ModelAttribute("article") Article articleForm,
                                @RequestParam(value = "topics", required = false) List<Long> topicIds,
                                HttpSession session) {
        Article article = articleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với id: " + id));

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !article.getAuthor().getUsername().equals(currentUser.getUsername())) {
            return "redirect:/articles/" + id;
        }

        // Cập nhật dữ liệu
        article.setTitle(articleForm.getTitle());
        article.setContent(articleForm.getContent());

        if (topicIds != null) {
            List<Topic> selectedTopics = topicService.findByIds(topicIds);
            article.setTopics(selectedTopics);
        }

        articleService.save(article);

        return "redirect:/articles/" + article.getId();
    }

    
    // Hiển thị trang confirm xóa
    @GetMapping("/articles/{id}/delete")
    public String confirmDelete(@PathVariable Long id, Model model, HttpSession session) {
        Article article = articleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với id: " + id));

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !article.getAuthor().getUsername().equals(currentUser.getUsername())) {
            return "redirect:/articles/" + id; // chỉ author mới được xóa
        }

        model.addAttribute("article", article);
        return "confirm"; // file confirm.html
    }

    // Xử lý POST xóa
    @PostMapping("/articles/{id}/delete")
    public String deleteArticle(@PathVariable Long id, HttpSession session) {
        Article article = articleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với id: " + id));

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !article.getAuthor().getUsername().equals(currentUser.getUsername())) {
            return "redirect:/articles/" + id;
        }

        articleService.deleteById(id);
        return "redirect:/"; // quay về trang chủ sau khi xóa
    }
    
}
