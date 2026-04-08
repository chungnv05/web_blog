package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);   // ✅ trả về Optional
    List<User> findAllByOrderByMarkDesc();            // ✅ danh sách theo mark giảm dần
    Page<User> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    boolean existsByUsername(String username);

    // phân quyền
    List<User> findByRole(Role role);
}
