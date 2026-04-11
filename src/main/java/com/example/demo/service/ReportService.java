package com.example.demo.service;

import com.example.demo.entity.Report;
import com.example.demo.entity.Article;
import com.example.demo.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // Lấy tất cả report
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // Tìm report theo ID
    public Optional<Report> findById(Integer id) {
        return reportRepository.findById(id);
    }

    // Lưu report
    public Report save(Report report) {
        return reportRepository.save(report);
    }

    // Xóa report theo ID
    public void deleteById(Integer id) {
        reportRepository.deleteById(id);
    }

    // Lấy tất cả report của một bài viết
    public List<Report> findByArticle(Article article) {
        return reportRepository.findByArticle(article);
    }

    // Xóa tất cả report của một bài viết
    public void deleteByArticle(Article article) {
        List<Report> reports = findByArticle(article);
        for (Report report : reports) {
            deleteById(report.getId());
        }
    }
}

