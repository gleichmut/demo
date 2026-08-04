package com.example.demo.dto;

import lombok.Getter;

@Getter
public class ProductResponse {
    private Long id;
    private String title;

    public ProductResponse(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public ProductResponse() {
    }

}
