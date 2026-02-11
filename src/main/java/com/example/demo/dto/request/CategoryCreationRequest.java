package com.example.demo.dto.request;

import com.example.demo.entity.Category;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreationRequest {
    @NotBlank(message = "Tên danh mục không được để trốgn")
    @Size(max = 50, min = 3, message = "Tên danh mục phải từ 3 đến 50 ký tự")
    private String name;

    private String description;

//    private Long parentId;
}
