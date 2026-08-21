package com.example.demo.controller;

import com.example.demo.dto.ProductCreateRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
@Tag(name = "ProductController", description = "Контроллер продукта.")
public class ProductController {
    public ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/createProduct")
    @Operation(summary = "Создать продукт", description = "Создание нового продукта.")
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "Успешно создан продукт."
//            ),
//            @ApiResponse(
//                    responseCode = "500",
//                    description = "Внутренняя ошибка сервера."
//            )
//    })
    public ProductResponse createProduct(@RequestBody ProductCreateRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping("/findAllProducts")
    @Operation(summary = "Получить все продукты", description = "Возвращает список всех продуктов.")
    public ResponseEntity<List<ProductResponse>> findAllProducts() {
        List<ProductResponse> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/updateProduct/{id}")
    @Operation(summary = "Обновить продукт", description = "Обновляет продукт по переданному id.")
    public ProductResponse updateCategory(
            @PathVariable Long id,
            @RequestBody ProductCreateRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/deleteProduct/{id}")
    @Operation(summary = "Удалить продукт", description = "Удаляет продукт по переданному id.")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "Продукт с id " + id + " успешно удален.";
    }

    @GetMapping("/findProductById/{id}")
    @Operation(summary = "Получить продукт по id", description = "Получает продукт по переданному id.")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable Long id) {
        ProductResponse product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/deleteAllProducts")
    @Operation(summary = "Удалить все продукты", description = "Удаляет все продукты.")
    public ResponseEntity<String> deleteAllProducts() {
        long start = System.currentTimeMillis();
        productService.deleteAllProducts();
        long time = System.currentTimeMillis() - start;
        return ResponseEntity.ok("Все продукты удалены за " + time + " мс");
    }

//    @PostMapping("/exception")
//    @Operation(summary = "Ошибка", description = "Выбрасывание исключения")
//    @ApiResponse(responseCode = "200", description = "Выброшено исключение")
//    public ProductResponse throwException() {
//        throw new RuntimeException("Ошибочка");
//    }

//    @PostMapping("/exception2")
//    @Operation(summary = "Ошибка2", description = "Выбрасывание исключения")
//    @ApiResponse(responseCode = "200", description = "Выброшено исключение")
//    public ProductResponse throwException2() {
//        throw new RuntimeException("Ошибочка2");
//    }

}
