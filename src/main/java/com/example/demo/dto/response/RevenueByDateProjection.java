package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RevenueByDateProjection {
    LocalDate getDate();
    BigDecimal getRevenue();
}
