package com.example.demo.service;

import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.exceptions.CategoryExists;
import com.example.demo.exceptions.CategoryNotFound;
import com.example.demo.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

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

    public List<Category> getAllCategories() {
        if (categoryRepository.findAll().isEmpty()) {
            throw new CategoryNotFound("Список категорий пуст или категории не найдены.");
        }
        return categoryRepository.findAll();
    }

    public CategoryResponse updateCategory(Long id, CategoryCreateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryExists("Категория: " + request.getName() + " не найдена."));
        category.setName(request.getName());
        Category updatedCategory = categoryRepository.save(category);
        return new CategoryResponse(updatedCategory.getId(), updatedCategory.getName());
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryExists("Категория с id = " + id + " не найдена.");
        }
        categoryRepository.deleteById(id);
    }
}
