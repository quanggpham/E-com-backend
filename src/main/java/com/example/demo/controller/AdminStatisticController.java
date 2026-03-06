package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.OverviewStatisticResponse;
import com.example.demo.dto.response.RevenueByDateProjection;
import com.example.demo.dto.response.TopProductProjection;
import com.example.demo.enums.OrderStatus;
import com.example.demo.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.WeekFields.ISO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/statistics")
public class AdminStatisticController {
    private final StatisticService statisticService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OverviewStatisticResponse>> getOverviewStatistic(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) OrderStatus status
            ) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponse.<OverviewStatisticResponse>builder()
                            .message("Lấy dữ liệu thống kê tổng quan thành công")
                            .data(statisticService.getOverviewStatistic(status, startDate, endDate))
                            .status(HttpStatus.OK.value())
                            .build()
            );
    }

    @GetMapping("/revenue-by-date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RevenueByDateProjection>>> getRevenueByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<RevenueByDateProjection>>builder()
                        .message("Lấy dữ liệu biểu đồ doanh thu thành công")
                        .status(HttpStatus.OK.value())
                        .data(statisticService.getRevenueByDate(startDate, endDate))
                        .build()
        );
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TopProductProjection>>> getTopSellingProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<TopProductProjection>>builder()
                        .message("Lấy dữ liệu top sản phẩm bán chạy thành công")
                        .status(HttpStatus.OK.value())
                        .data(statisticService.getTopSellingProducts(limit, startDate, endDate))
                        .build()
        );
    }
}
