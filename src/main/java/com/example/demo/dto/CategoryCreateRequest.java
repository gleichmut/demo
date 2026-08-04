package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

// передать один параметр, а не все параметры
@Getter
public class CategoryCreateRequest {
    @Schema(name = "name", example = "Кошки")
    private String name;

    public CategoryCreateRequest(String name) {
        this.name = name;
    }

    public CategoryCreateRequest() {
    }

}
