package com.example.demo.controller;

import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@Tag(name = "CategoryController", description = "Контроллер категории")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/createCategory")
    @Operation(summary = "Создать категорию", description = "Создание новой категории")
    @ApiResponse(responseCode = "200", description = "Успешно создана категория")
    public CategoryResponse createCategory(@RequestBody CategoryCreateRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping("/getAllCategories")
    @Operation(summary = "Получить все категории", description = "Возвращает список всех категорий")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/test")
    public String test() {
        return "TEST";
    }

    @PutMapping("/updateCategory/{id}")
    @Operation(summary = "Обновить категорию", description = "Обновляет категорию по id")
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryCreateRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/deleteCategory/{id}")
    @Operation(summary = "Удалить категорию", description = "Удаляет категорию по id")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "Категория с id " + id + " успешно удалена";
    }
}


// model - repository (бд) - service (валидация, обработка, методы из репозитория) - controller (get data, queries - в сервис - в репозиторий - а он к моделям)