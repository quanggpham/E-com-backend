package com.example.demo.dto.response;

import com.example.demo.enums.ReviewStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long orderItemId;
    private Long userId;
    private String userName;
    private Integer rating;
    private String content;
    private ReviewStatus status;
    private Integer reportCount;
    private String sellerReply;
    private String rejectionReason;
    private LocalDateTime sellerReplyAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
