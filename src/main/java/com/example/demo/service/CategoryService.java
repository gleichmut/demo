package com.example.demo.service;

import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.exceptions.CategoryExists;
import com.example.demo.exceptions.CategoryNotFound;
import com.example.demo.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryCreateRequest request) {
        // Пытаемся найти категорию по имени. Если она УЖЕ есть -> кидаем исключение.
        // findByName вернет Optional. Если Optional не пустой (категория есть), мы падаем в orElseThrow.
        categoryRepository.findByName(request.getName())
                .ifPresent(category -> {
                    throw new CategoryExists("Категория: " + request.getName() + " уже существует.");
                });

        // Если код дошел до сюда, значит категории нет. Создаем и сохраняем.
        Category category = new Category();
        category.setName(request.getName());

        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponse(savedCategory.getId(), savedCategory.getName());
    }

    public List<Category> findAllCategories() {
        return Optional.of(categoryRepository.findAll()) // Оборачиваем список в Optional
                .filter(categories -> !categories.isEmpty()) // Если список не пуст - оставляем
                .orElseThrow(() -> new CategoryNotFound("Список категорий пуст.")); // Если пуст - кидаем ошибку
    }

    public CategoryResponse updateCategory(Long id, CategoryCreateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryExists("Категория: " + request.getName() + " не найдена."));
        category.setName(request.getName());
        Category updatedCategory = categoryRepository.save(category);
        return new CategoryResponse(updatedCategory.getId(), updatedCategory.getName());
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFound("Категория с id = " + id + " не найдена."));

        // Если существует - удаляем
        categoryRepository.deleteById(id);
    }

    public CategoryResponse findCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFound("Категория не найдена"));
        return new CategoryResponse(category);  // ✅
    }
}
