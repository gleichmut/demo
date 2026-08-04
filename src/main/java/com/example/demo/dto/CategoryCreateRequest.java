package com.example.demo.dto;

import lombok.Getter;

// передать один параметр, а не все параметры
@Getter
public class CategoryCreateRequest {
    private String name;

    public CategoryCreateRequest(String name) {
        this.name = name;
    }

    public CategoryCreateRequest() {
    }

}
