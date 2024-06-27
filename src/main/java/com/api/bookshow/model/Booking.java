package com.api.bookshow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private int numberOfSeats;
    private double totalPrice;
    private double gstAmount;
    private LocalDateTime bookingTime;
    private BookingStatus status;

    @JsonIgnore
    @OneToOne
    private Coupon coupon;
}
