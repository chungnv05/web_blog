package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class PasswordResetController {

    private final PasswordResetService resetService;

    public PasswordResetController(PasswordResetService resetService) {
        this.resetService = resetService;
    }

    // Hiển thị trang nhập email
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }

    // Xử lý gửi mail
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, 
                                        HttpServletRequest request, 
                                        Model model) {
        String siteUrl = request.getRequestURL().toString().replace(request.getServletPath(), "");
        boolean success = resetService.createPasswordResetTokenForUser(email, siteUrl);
        
        if (success) {
            model.addAttribute("message", "Chúng tôi đã gửi link đặt lại mật khẩu vào email của bạn. Vui lòng kiểm tra.");
        } else {
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
        }
        return "forgot_password";
    }

    // Hiển thị trang nhập mật khẩu mới (từ link email)
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<User> user = resetService.getUserByPasswordResetToken(token);
        if (user.isEmpty()) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "error";
        }
        
        model.addAttribute("token", token);
        return "reset_password";
    }

    // Xử lý đổi mật khẩu mới
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, 
                                       @RequestParam("password") String password,
                                       RedirectAttributes redirectAttributes) {
        Optional<User> user = resetService.getUserByPasswordResetToken(token);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi, vui lòng thử lại.");
            return "redirect:/forgot-password";
        }

        resetService.updatePassword(user.get(), password);
        redirectAttributes.addFlashAttribute("message", "Mật khẩu đã được thay đổi thành công! Vui lòng đăng nhập.");
        return "redirect:/login";
    }
}
