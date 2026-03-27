package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerReplyRequest {

    @NotBlank(message = "reply khong duoc de trong")
    @Size(max = 1000, message = "reply toi da 1000 ky tu")
    private String reply;
}
