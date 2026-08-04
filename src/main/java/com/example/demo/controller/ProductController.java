package com.example.demo.controller;

import com.example.demo.dto.ProductCreateRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/product")
@Tag(name = "ProductController", description = "Контроллер продукта")
public class ProductController {
    public ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/createProduct")
    @Operation(summary = "Создать продукт", description = "Возвращает список всех продуктов")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно создан продукт"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "мяу мяу"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    public ProductResponse createProduct(@RequestBody ProductCreateRequest request) {
        return productService.createProduct(request);
    }

    @PostMapping("/exception")
    @Operation(summary = "Ошибка", description = "Выбрасывание исключения")
    @ApiResponse(responseCode = "200", description = "Выброшено исключение")
    public ProductResponse throwException() {
        throw new RuntimeException("Ошибочка");
    }

    @PostMapping("/exception2")
    @Operation(summary = "Ошибка2", description = "Выбрасывание исключения")
    @ApiResponse(responseCode = "200", description = "Выброшено исключение")
    public ProductResponse throwException2() {
        throw new RuntimeException("Ошибочка2");
    }
}
