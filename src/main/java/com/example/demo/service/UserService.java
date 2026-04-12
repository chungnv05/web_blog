package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public UserService(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) return;
        
        User follower = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("Follower not found"));
        User following = userRepository.findById(followingId).orElseThrow(() -> new RuntimeException("User to follow not found"));
        
        follower.getFollowing().add(following);
        userRepository.save(follower);

        notificationService.createNotification(
            following,
            follower,
            null,
            "FOLLOW",
            follower.getName() + " đã bắt đầu theo dõi bạn"
        );
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("Follower not found"));
        User following = userRepository.findById(followingId).orElseThrow(() -> new RuntimeException("User to unfollow not found"));
        
        follower.getFollowing().remove(following);
        userRepository.save(follower);
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElse(null);
        if (follower == null) return false;
        return follower.getFollowing().stream().anyMatch(u -> u.getId().equals(followingId));
    }

    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) return;
        
        User follower = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("Follower not found"));
        User following = userRepository.findById(followingId).orElseThrow(() -> new RuntimeException("User to follow not found"));
        
        follower.getFollowing().add(following);
        userRepository.save(follower);
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("Follower not found"));
        User following = userRepository.findById(followingId).orElseThrow(() -> new RuntimeException("User to unfollow not found"));
        
        follower.getFollowing().remove(following);
        userRepository.save(follower);
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId).orElse(null);
        if (follower == null) return false;
        return follower.getFollowing().stream().anyMatch(u -> u.getId().equals(followingId));
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

    // Tìm user theo username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Tìm tất cả user theo mark giảm dần
    public List<User> findAllOrderByMarkDesc() {
        return userRepository.findAllByOrderByMarkDesc();
    }

    // Tìm kiếm user theo tên (phân trang)
    public Page<User> findByNameContainingIgnoreCase(String keyword, Pageable pageable) {
        return userRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
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
