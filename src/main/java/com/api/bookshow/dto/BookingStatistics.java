package com.api.bookshow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BookingStatistics {
    private int totalBookings;
    private BigDecimal totalMoneyCollected;
    private BigDecimal totalGSTCollected;
}
