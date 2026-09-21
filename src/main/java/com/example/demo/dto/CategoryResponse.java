package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryResponse {
    private Long id;
    private String name;

//    public CategoryResponse(Category category) {
//        this.id = category.getId();
//        this.name = category.getName();
//    }
}

