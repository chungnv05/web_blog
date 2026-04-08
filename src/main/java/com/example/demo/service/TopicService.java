package com.example.demo.service;

import com.example.demo.entity.Topic;
import com.example.demo.repository.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TopicService {
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    // Lấy tất cả topic
    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    // Phân trang topic
    public Page<Topic> findAll(Pageable pageable) {
        return topicRepository.findAll(pageable);
    }

    // Tìm topic theo ID
    public Optional<Topic> findById(Long id) {
        return topicRepository.findById(id);
    }

    // Lưu topic
    public Topic save(Topic topic) {
        return topicRepository.save(topic);
    }

    // Xóa topic theo ID
    public void deleteById(Long id) {
        topicRepository.deleteById(id);
    }

    // Tìm topic theo tên
    public Topic findByName(String name) {
        return topicRepository.findByName(name);
    }

    // ✅ Tìm nhiều topic theo danh sách ID
    public List<Topic> findByIds(List<Long> ids) {
        return topicRepository.findByIdIn(ids);
    }
    
    // getter/ setter
    public void createTopic(String name) {
        Topic topic = new Topic();
        topic.setName(name);
        topicRepository.save(topic);
    }
    
    public void deleteTopic(Long id) {
        topicRepository.deleteById(id);
    }
}
