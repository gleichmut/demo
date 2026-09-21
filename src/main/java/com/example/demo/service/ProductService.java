package com.example.demo.service;

import com.example.demo.dto.ProductCreateRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exceptions.CategoryNotFound;
import com.example.demo.exceptions.ProductNotFound;
import com.example.demo.mapper.ProductMapper;
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
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    public ProductResponse createProduct(ProductCreateRequest request) {
        // Находим категорию по ID
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFound("Категория с id = " + request.getCategoryId() + " не найдена."));

        // Создаем продукт
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        return productMapper.toResponse(productRepository.save(product));
    }

    public ProductResponse findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFound("Продукт не найден."));
        return productMapper.toResponse(product);
    }

    public List<ProductResponse> findAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new ProductNotFound("Список продуктов пуст.");
        }
        return productMapper.toResponseList(products);
    }

    public ProductResponse updateProduct(Long id, ProductCreateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFound("Продукт с id = " + id + " не найден."));

        productMapper.updateEntity(request, product);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFound("Категория с id = " + request.getCategoryId() + " не найдена"));
            product.setCategory(category);
        }
        return productMapper.toResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        // Проверяем: если продукта НЕТ в базе -> кидаем ошибку
        productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFound("Продукт с id = " + id + " не найден."));

        // Если код дошел до сюда, значит продукт есть -> удаляем
        productRepository.deleteById(id);
    }

    @Transactional(timeout = 5)
    public void deleteAllProducts() {
        productRepository.deleteAllInBatch();
    }
}

// категорию в дто
// лист в дто
// и обратно из дто
// тесты
