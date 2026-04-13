package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.User;          // entity User của bạn
import com.example.demo.entity.Role;          // enum Role (USER, ADMIN)
import com.example.demo.repository.UserRepository; 
import java.time.LocalDate;

@SpringBootApplication
public class WebSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebSpringBootApplication.class, args);
	}
        // Bean khởi tạo admin mặc định - phân quyền
        @Bean
        CommandLineRunner init(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByRole(Role.ADMIN).isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setName("Super Admin");
            admin.setEmail("admin@example.com");
            admin.setGender("Nam");
            admin.setBirth(LocalDate.of(1990, 1, 1));
            admin.setMark(0);
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            }
        };
    }

}
