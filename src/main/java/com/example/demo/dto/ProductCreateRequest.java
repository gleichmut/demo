package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ProductCreateRequest {
    @Schema(name = "Название продукта", example = "Кошечка черная")
    private String title;

    public ProductCreateRequest(String title) {
        this.title = title;
    }

    public ProductCreateRequest() {
    }

}
