package com.example.demo.controller;

import com.example.demo.entity.Article;
import com.example.demo.entity.Series;
import com.example.demo.entity.User;
import com.example.demo.service.ArticleService;
import com.example.demo.service.SeriesService;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/series")
public class SeriesController {

    private final SeriesService seriesService;
    private final UserService userService;
    private final ArticleService articleService;

    public SeriesController(SeriesService seriesService, UserService userService, ArticleService articleService) {
        this.seriesService = seriesService;
        this.userService = userService;
        this.articleService = articleService;
    }

    @GetMapping("/create")
    public String showCreateForm(HttpSession session, Model model) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Tạo một đối tượng Series mới để binding với form
        Series series = new Series();
        model.addAttribute("series", series);

        // render file templates/users/create_series.html
        return "users/create_series";
    }

    @PostMapping("/save")
    public String saveSeries(@ModelAttribute("series") Series series,
                             HttpSession session) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Gán chủ sở hữu là user hiện tại
        series.setOwner(currentUser);

        // Lưu series vào DB
        seriesService.save(series);

        // Quay về trang cá nhân
        return "redirect:/user";
    }

    @GetMapping("/{id}")
    public String seriesDetail(@PathVariable Long id,
                               Model model) {
        // Lấy series từ DB
        Series series = seriesService.findById(id)
                                     .orElseThrow(() -> new RuntimeException("Series not found"));

        // Lấy danh sách bài viết trong series
        List<Article> articles = series.getArticles() != null ? series.getArticles() : new ArrayList<>();

        model.addAttribute("series", series);
        model.addAttribute("articles", articles);

        // render file templates/series/detail.html
        return "series/detail";
    }

    @GetMapping("/list")
    public String showListSeries(HttpSession session, Model model) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Nạp lại user từ DB để có đầy đủ series
        User user = userService.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy tất cả series của user
        List<Series> seriesList = seriesService.getAllByUser(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("seriesList", seriesList);

        // render file templates/users/list_series.html
        return "users/list_series";
    }

    @GetMapping("/explore")
    public String exploreSeries(Model model) {
        // Lấy tất cả series trong hệ thống
        List<Series> allSeries = seriesService.findAll();

        model.addAttribute("seriesList", allSeries);

        // render file templates/series/explore.html
        return "series/explore";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               HttpSession session,
                               Model model) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Lấy series từ DB
        Series series = seriesService.findById(id)
                .orElseThrow(() -> new RuntimeException("Series not found"));

        // Kiểm tra xem user có quyền edit series này không
        if (!series.getOwner().getId().equals(currentUser.getId())) {
            return "redirect:/series/list";
        }

        model.addAttribute("series", series);
        // render file templates/users/edit_series.html
        return "users/edit_series";
    }

    @PostMapping("/{id}/update")
    public String updateSeries(@PathVariable Long id,
                               @ModelAttribute("series") Series updatedSeries,
                               HttpSession session) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Lấy series cũ từ DB
        Series existingSeries = seriesService.findById(id)
                .orElseThrow(() -> new RuntimeException("Series not found"));

        // Kiểm tra xem user có quyền edit series này không
        if (!existingSeries.getOwner().getId().equals(currentUser.getId())) {
            return "redirect:/series/list";
        }

        // Cập nhật thông tin
        existingSeries.setTitle(updatedSeries.getTitle());
        existingSeries.setDescription(updatedSeries.getDescription());
        existingSeries.setCategory(updatedSeries.getCategory());

        // Lưu lại vào DB
        seriesService.save(existingSeries);

        // Quay về trang danh sách series
        return "redirect:/series/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteSeries(@PathVariable Long id,
                               HttpSession session) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Lấy series từ DB
        Series series = seriesService.findById(id)
                .orElseThrow(() -> new RuntimeException("Series not found"));

        // Kiểm tra xem user có quyền delete series này không
        if (!series.getOwner().getId().equals(currentUser.getId())) {
            return "redirect:/series/list";
        }

        // Xóa series
        seriesService.deleteById(id);

        // Quay về trang danh sách series
        return "redirect:/series/list";
    }

}