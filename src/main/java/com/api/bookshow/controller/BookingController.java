package com.api.bookshow.controller;

import com.api.bookshow.dto.BookingStatistics;
import com.api.bookshow.dto.CancellationResponse;
import com.api.bookshow.model.Booking;
import com.api.bookshow.service.BookingService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestMapping("/api/booking")
@RestController
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @RolesAllowed("ADMIN")
    @GetMapping("/statistics")
    public BookingStatistics getBookingStatistics(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<Booking> bookings;

        if (eventId != null) {
            bookings = bookingService.getBookingsByEvent(eventId);
        } else if (startDate != null && endDate != null) {
            LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);
            bookings = bookingService.getBookingsByDateRange(start, end);
        } else {
            bookings = bookingService.getAllBookings();
        }

        return bookingService.getBookingStatistics(bookings);
    }

    @PostMapping("/{bookingId}/cancel")
    public CancellationResponse cancelBooking(@PathVariable Long bookingId) {
        return bookingService.cancelBooking(bookingId);
    }
}
