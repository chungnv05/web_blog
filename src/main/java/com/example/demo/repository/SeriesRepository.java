package com.example.demo.repository;


import com.example.demo.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findByOwnerId(Long ownerId);
    List<Series> findDistinctByArticles_Id(Long articleId);
}
