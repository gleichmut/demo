package com.example.demo.mapper;

import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    Category toEntity(CategoryCreateRequest categoryCreateRequest);

    List<CategoryResponse> toResponseList(List<Category> categories);
}
