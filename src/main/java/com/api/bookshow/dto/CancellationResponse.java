package com.api.bookshow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CancellationResponse {
    private BigDecimal refundAmount;
    private BigDecimal gstAmount;
}
