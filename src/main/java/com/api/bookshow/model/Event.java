package com.api.bookshow.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // Movies, Concerts, Live_Show
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxOccupancy;
    private int availableSeats;
    private double price;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    private Theater theater;
}
