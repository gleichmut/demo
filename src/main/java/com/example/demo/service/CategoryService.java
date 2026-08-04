package com.example.demo.service;

import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.exceptions.CategoryExists;
import com.example.demo.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryCreateRequest request) {
        Category category = new Category();
        category.setName(request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            throw new CategoryExists("Категория: " + request.getName() + " уже существует.");
        } else {
            categoryRepository.save(category);
        }
        return new CategoryResponse(category.getId(), category.getName());
    }
}
