package com.example.demo.service;

import com.example.demo.entity.PasswordResetToken;
import com.example.demo.entity.User;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository, 
                                UserRepository userRepository, 
                                EmailService emailService, 
                                PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public boolean createPasswordResetTokenForUser(String email, String siteUrl) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Xóa token cũ nếu có
            tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

            String token = UUID.randomUUID().toString();
            PasswordResetToken myToken = new PasswordResetToken(token, user, 30); // 30 phút hết hạn
            tokenRepository.save(myToken);

            String resetUrl = siteUrl + "/reset-password?token=" + token;
            String content = "Chào " + user.getName() + ",\n\n"
                    + "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng click vào link bên dưới để thực hiện:\n"
                    + resetUrl + "\n\n"
                    + "Link này sẽ hết hạn sau 30 phút.\n"
                    + "Nếu bạn không yêu cầu điều này, hãy bỏ qua email.";

            emailService.sendEmail(user.getEmail(), "Yêu cầu đặt lại mật khẩu", content);
            return true;
        }
        return false;
    }

    public Optional<User> getUserByPasswordResetToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isExpired())
                .map(PasswordResetToken::getUser);
    }

    @Transactional
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Sau khi đổi xong thì xóa token
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
    }
}
