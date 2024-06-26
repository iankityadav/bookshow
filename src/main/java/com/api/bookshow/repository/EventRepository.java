package com.api.bookshow.repository;

import com.api.bookshow.model.Event;
import com.api.bookshow.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, PagingAndSortingRepository<Event, Long> {
    boolean existsByTheaterAndStartTimeAndEndTime(Theater theater, LocalDateTime startTime, LocalDateTime endTime);
}
