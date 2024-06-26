package com.api.bookshow.service;

import com.api.bookshow.dto.BookingStatistics;
import com.api.bookshow.dto.CancellationResponse;
import com.api.bookshow.model.Booking;
import com.api.bookshow.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getBookingsByEvent(Long eventId) {
        return bookingRepository.findByEventId(eventId);
    }

    public List<Booking> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findByDateRange(startDate, endDate);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public BookingStatistics getBookingStatistics(List<Booking> bookings) {
        int totalBookings = bookings.size();
        BigDecimal totalMoneyCollected = bookings.stream()
                .map(Booking::getTotalPrice).map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGSTCollected = bookings.stream()
                .map(Booking::getTotalPrice).map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BookingStatistics(totalBookings, totalMoneyCollected, totalGSTCollected);
    }

    public CancellationResponse cancelBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isEmpty()) {
            throw new IllegalArgumentException("Booking not found.");
        }

        Booking booking = optionalBooking.get();
        LocalDateTime eventStartTime = booking.getEvent().getStartTime();
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(now, eventStartTime);
        BigDecimal refundPercentage = getRefundPercent(duration);

        BigDecimal totalAmount = BigDecimal.valueOf(booking.getTotalPrice());
        BigDecimal refundAmount = totalAmount.multiply(refundPercentage);
        BigDecimal gstAmount = BigDecimal.valueOf(booking.getGstAmount());

        // Release seats
        // booking.getEvent().releaseSeats(booking.getSeatsBooked());

        // Update booking status
        // booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return new CancellationResponse(refundAmount, gstAmount);
    }

    private static BigDecimal getRefundPercent(Duration duration) {
        long hoursUntilEvent = duration.toHours();

        if (hoursUntilEvent < 2) {
            throw new IllegalArgumentException("Cancellation is not allowed if the event is starting in less than 2 hours.");
        }

        BigDecimal refundPercentage;

        if (hoursUntilEvent >= 24) {
            refundPercentage = new BigDecimal("0.80");
        } else if (hoursUntilEvent >= 12) {
            refundPercentage = new BigDecimal("0.50");
        } else {
            refundPercentage = new BigDecimal("0.10");
        }
        return refundPercentage;
    }
}
