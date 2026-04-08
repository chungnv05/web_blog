package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Lấy tất cả user
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Phân trang user
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // Tìm user theo ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Lưu user
    public User save(User user) {
        return userRepository.save(user);
    }

    // Xóa user theo ID
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // Tìm user theo username (Optional)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Tìm tất cả user theo mark giảm dần
    public List<User> findAllOrderByMarkDesc() {
        return userRepository.findAllByOrderByMarkDesc();
    }

    // ✅ Tìm kiếm user theo tên (phân trang)
    public Page<User> findByNameContainingIgnoreCase(String keyword, Pageable pageable) {
        return userRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
    // getter / setter
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void createUser(User user) {
        userRepository.save(user);
    }
    
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}
