package com.jobify.repository;

import com.jobify.entities.HrMailSentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HrMailSentHistoryRepository extends JpaRepository<HrMailSentHistory, Long> {

    @Query("""
            SELECT h FROM HrMailSentHistory h
            JOIN FETCH h.hrUserMapping m
            JOIN FETCH m.hr
            JOIN FETCH m.user
            ORDER BY h.createdTime DESC
            """)
    List<HrMailSentHistory> findAllOrderByCreatedTimeDesc();
}
