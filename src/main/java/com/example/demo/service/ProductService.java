package com.example.demo.service;

import com.example.demo.dto.ProductCreateRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exceptions.CategoryNotFound;
import com.example.demo.exceptions.ProductNotFound;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        // Находим категорию по ID
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFound("Категория с id = " + request.getCategoryId() + " не найдена."));

        // Создаем продукт
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setCategory(category); // Привязываем категорию

        Product saved = productRepository.save(product);
        return new ProductResponse(saved.getId(), saved.getTitle(), saved.getPrice(), category.getId());
    }

    public List<ProductResponse> findAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new ProductNotFound("Список продуктов пуст.");
        }
        return products.stream()
                .map(ProductResponse::new)  // Преобразуем каждый продукт
                .collect(Collectors.toList());
    }

    public ProductResponse updateProduct(Long id, ProductCreateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFound("Продукт с id = " + id + " не найден."));

        // Обновляем поля
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());

        // Если нужно обновить категорию
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Категория с id = " + request.getCategoryId() + " не найдена"));
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);
        return new ProductResponse(
                updatedProduct.getId(),
                updatedProduct.getTitle(),
                updatedProduct.getPrice(),
                updatedProduct.getId()
        );
    }

    public void deleteProduct(Long id) {
        // Проверяем: если продукта НЕТ в базе -> кидаем ошибку
        productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFound("Продукт с id = " + id + " не найден."));

        // Если код дошел до сюда, значит продукт есть -> удаляем
        productRepository.deleteById(id);
    }

    public ProductResponse findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFound("Продукт не найден"));
        return new ProductResponse(product);  // Возвращаем DTO
    }

    @Transactional(timeout = 5)
    public void deleteAllProducts() {
        productRepository.deleteAllInBatch();
    }
}