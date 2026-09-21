package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


// передать один параметр, а не все параметры
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryCreateRequest {
    @Schema(name = "name", example = "Кошки")
    private String name;
}
