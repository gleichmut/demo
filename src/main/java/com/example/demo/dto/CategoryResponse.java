package com.example.demo.dto;

import lombok.Getter;

@Getter
public class CategoryResponse {
    private Long id;
    private String name;

    public CategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryResponse() {
    }

}

