package com.example.demo.dto.request;

import com.example.demo.enums.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminReviewStatusRequest {

    @NotNull(message = "status khong duoc de trong")
    private ReviewStatus status;

    @Size(max = 500, message = "rejectionReason toi da 500 ky tu")
    private String rejectionReason;
}
