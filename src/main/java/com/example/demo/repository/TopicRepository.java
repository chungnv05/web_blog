package com.example.demo.repository;

import com.example.demo.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    Topic findByName(String name);
    List<Topic> findByIdIn(List<Long> ids);
}
