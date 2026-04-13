package com.example.demo.service;

import com.example.demo.entity.Series;
import com.example.demo.repository.SeriesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SeriesService {

    private final SeriesRepository seriesRepository;

    public SeriesService(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    public List<Series> findAll() {
        return seriesRepository.findAll();
    }

    public List<Series> getAllByUser(Long userId) {
        return seriesRepository.findByOwnerId(userId);
    }

    public Optional<Series> findById(Long id) {
        return seriesRepository.findById(id);
    }

    public List<Series> findByArticleId(Long articleId) {
        return seriesRepository.findDistinctByArticles_Id(articleId);
    }

    public Series save(Series series) {
        return seriesRepository.save(series);
    }

    @Transactional
    public void deleteById(Long id) {
        Series series = seriesRepository.findById(id).orElse(null);
        if (series == null) {
            return;
        }
        series.getArticles().clear();
        seriesRepository.delete(series);
    }

}
