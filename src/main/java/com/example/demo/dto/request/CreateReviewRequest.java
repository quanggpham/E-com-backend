package com.example.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {

    @NotNull(message = "orderItemId khong duoc de trong")
    private Long orderItemId;

    @NotNull(message = "rating khong duoc de trong")
    @Min(value = 1, message = "rating toi thieu 1")
    @Max(value = 5, message = "rating toi da 5")
    private Integer rating;

    @NotBlank(message = "content khong duoc de trong")
    @Size(max = 2000, message = "content toi da 2000 ky tu")
    private String content;
}
