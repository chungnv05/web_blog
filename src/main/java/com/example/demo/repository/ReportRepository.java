package com.example.demo.repository;

import com.example.demo.entity.Report;
import com.example.demo.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByArticle(Article article);
}

