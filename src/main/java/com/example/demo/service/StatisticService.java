package com.example.demo.service;

import com.example.demo.dto.response.OverviewStatisticResponse;
import com.example.demo.dto.response.RevenueByDateProjection;
import com.example.demo.dto.response.TopProductProjection;
import com.example.demo.entity.Order;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticService {
    private final OrderRepository orderRepository;

    public OverviewStatisticResponse getOverviewStatistic(OrderStatus status, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = LocalDate.now();
        } else if (startDate == null) {
            startDate = endDate.withDayOfMonth(1);
        } else if (endDate == null) {
            endDate = LocalDate.now();
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }

        if (status == null) {
            status = OrderStatus.COMPLETED;
        }

        LocalDateTime  startDateTime = startDate.atStartOfDay();
        LocalDateTime  endDateTime = endDate.atTime(LocalTime.MAX);

        return orderRepository.getOverviewStatistic(status, startDateTime, endDateTime);
    }

    public List<RevenueByDateProjection> getRevenueByDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = LocalDate.now();
        } else if (startDate == null) {
            startDate = endDate.withDayOfMonth(1);
        } else if (endDate == null) {
            endDate = LocalDate.now();
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }

        LocalDateTime  startDateTime = startDate.atStartOfDay();
        LocalDateTime  endDateTime = endDate.atTime(LocalTime.MAX);

        return orderRepository.getRevenueByDate(OrderStatus.COMPLETED.name(), startDateTime, endDateTime);
    }

    public List<TopProductProjection> getTopSellingProducts(int limit , LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = LocalDate.now();
        } else if (startDate == null) {
            startDate = endDate.withDayOfMonth(1);
        } else if (endDate == null) {
            endDate = LocalDate.now();
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        int safeLimit = (limit <= 0 || limit > 100) ? 5 : limit;

        Pageable pageable = PageRequest.of(0, safeLimit, Sort.by("totalQuantity").descending());

        return orderRepository.getTopSellingProducts(OrderStatus.COMPLETED.name(), startDateTime, endDateTime, pageable);
    }
}
