package com.example.demo.service;

import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.exceptions.CategoryExists;
import com.example.demo.exceptions.CategoryNotFound;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public CategoryResponse createCategory(CategoryCreateRequest request) {
        // Пытаемся найти категорию по имени. Если она УЖЕ есть -> кидаем исключение.
        // findByName вернет Optional. Если Optional не пустой (категория есть), мы падаем в orElseThrow.
        categoryRepository.findByName(request.getName())
                .ifPresent(category -> {
                    throw new CategoryExists("Категория: " + request.getName() + " уже существует.");
                });

        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public CategoryResponse findCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFound("Категория не найдена"));
        return categoryMapper.toResponse(category);
    }
    public List<CategoryResponse> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new CategoryNotFound("Список категорий пуст.");
        }
        return categoryMapper.toResponseList(categories);
    }

    public CategoryResponse updateCategory(Long id, CategoryCreateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryExists("Категория: " + request.getName() + " не найдена."));
        category.setName(request.getName());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFound("Категория с id = " + id + " не найдена."));
        categoryRepository.deleteById(id);
    }
}
