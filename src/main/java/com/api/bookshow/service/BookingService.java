package com.api.bookshow.service;

import com.api.bookshow.dto.BookingStatistics;
import com.api.bookshow.dto.CancellationResponse;
import com.api.bookshow.model.*;
import com.api.bookshow.repository.BookingRepository;
import com.api.bookshow.repository.CouponRepository;
import com.api.bookshow.repository.EventRepository;
import com.api.bookshow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CouponRepository couponRepository;

    public List<Booking> getBookingsByEvent(Long eventId) {
        return bookingRepository.findByEventId(eventId);
    }

    /**
     * List out all the bookings within a date range
     *
     * @param startDate
     * @param endDate
     * @return list of bookings
     */
    public List<Booking> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findByDateRange(startDate, endDate);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Fetch the total amount and gst for all the bookings
     *
     * @param bookings
     * @return total amount and gst
     */
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

    /**
     * @param bookingId
     * @return amount to return
     */
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

    /**
     * Utility method to check for the refund percentage
     *
     * @param duration
     * @return percentage
     */
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


    public Booking bookTickets(Long eventId, Long userId, int numberOfTickets, String couponCode) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new IllegalArgumentException("Event not found");
        }

        Event event = eventOptional.get();

        if (event.getAvailableSeats() < numberOfTickets) {
            throw new IllegalArgumentException("Not enough available seats");
        }

        Optional<Users> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Users user = userOptional.get();

        BigDecimal totalPrice = BigDecimal.valueOf(event.getPrice()).multiply(BigDecimal.valueOf(numberOfTickets));
        BigDecimal gstRate = getGSTRate(EventType.valueOf(event.getType()), user.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        BigDecimal gstAmount = totalPrice.multiply(gstRate);
        totalPrice = totalPrice.add(gstAmount);

        if (couponCode != null && !couponCode.isEmpty()) {
            Optional<Coupon> couponOptional = couponRepository.findByCodeAndExpiryDateAfter(couponCode, LocalDate.now());
            if (couponOptional.isPresent()) {
                Coupon coupon = couponOptional.get();
                if (coupon.getMaxUsage() > 0) {
                    BigDecimal discount = totalPrice.multiply(BigDecimal.valueOf(coupon.getDiscountPercentage()));
                    totalPrice = totalPrice.subtract(discount);
                    coupon.setMaxUsage(coupon.getMaxUsage() - 1);
                    couponRepository.save(coupon);
                }
            }
        }

        event.setAvailableSeats(event.getAvailableSeats() - numberOfTickets);
        eventRepository.save(event);

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setUser(user);
        booking.setNumberOfSeats(numberOfTickets);
        booking.setTotalPrice(totalPrice.doubleValue());
        booking.setBookingTime(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    private BigDecimal getGSTRate(EventType eventType, LocalDate dateOfBirth) {
        int age = LocalDate.now().getYear() - dateOfBirth.getYear();
        if (age > 60) {
            return BigDecimal.ZERO;
        }
        return switch (eventType) {
            case MOVIE -> BigDecimal.valueOf(0.08);
            case CONCERT -> BigDecimal.valueOf(0.10);
            case LIVE_SHOW -> BigDecimal.valueOf(0.06);
            default -> throw new IllegalArgumentException("Unknown event type");
        };
    }
}
