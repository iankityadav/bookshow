package com.api.bookshow.repository;


import com.api.bookshow.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.event.id = :eventId")
    List<Booking> findByEventId(@Param("eventId") Long eventId);

    @Query("SELECT b FROM Booking b WHERE b.bookingTime BETWEEN :startDate AND :endDate")
    List<Booking> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
