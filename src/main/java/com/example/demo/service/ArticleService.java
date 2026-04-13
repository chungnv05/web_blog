package com.example.demo.service;

import com.example.demo.entity.Article;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.entity.Series;
import com.example.demo.service.history.ReadingHistoryService;
import com.example.demo.service.history.QuizHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ReportService reportService;
    private final NotificationService notificationService;
    private final ReadingHistoryService readingHistoryService;
    private final QuizHistoryService quizHistoryService;
    private final SeriesService seriesService;

    public ArticleService(ArticleRepository articleRepository,
                          ReportService reportService,
                          NotificationService notificationService,
                          ReadingHistoryService readingHistoryService,
                          QuizHistoryService quizHistoryService,
                          SeriesService seriesService) {
        this.articleRepository = articleRepository;
        this.reportService = reportService;
        this.notificationService = notificationService;
        this.readingHistoryService = readingHistoryService;
        this.quizHistoryService = quizHistoryService;
        this.seriesService = seriesService;
    }
    
    // lấy tất cả bài viết (không phân trang)
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    // Phân trang tất cả bài viết
    public Page<Article> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    // Tìm theo ID
    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

    // Lưu bài viết
    public Article save(Article article) {
        return articleRepository.save(article);
    }

    // Xóa bài viết
    @Transactional
    public void deleteById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với id: " + id));

        deleteArticleInternal(article, id);
    }

    private void deleteArticleInternal(Article article, Long id) {

        for (Series series : seriesService.findByArticleId(id)) {
            series.getArticles().removeIf(a -> a.getId().equals(id));
            seriesService.save(series);
        }

        reportService.deleteByArticle(article);
        notificationService.deleteByArticle(article);
        readingHistoryService.deleteByArticle(article);
        if (article.getQuiz() != null) {
            quizHistoryService.deleteByQuiz(article.getQuiz());
        }
        articleRepository.delete(article);
    }

    // Tìm bài viết theo tác giả
    public List<Article> findByAuthorId(Long authorId) {
        return articleRepository.findByAuthorId(authorId);
    }

    // Tìm kiếm theo tiêu đề (phân trang)
    public Page<Article> findByTitleContainingIgnoreCase(String keyword, Pageable pageable) {
        return articleRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    // Lọc theo topics
    public List<Article> findByTopicIds(List<Long> topicIds) {
        return articleRepository.findDistinctByTopics_IdIn(topicIds);
    }


    
    // getter / setter
    @Transactional
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết với id: " + id));
        deleteArticleInternal(article, id);
    }
}
