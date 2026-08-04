package com.example.demo.service;

import com.example.demo.dto.ProductCreateRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setTitle(request.getTitle());

        if (productRepository.existsByTitle(request.getTitle())) {
            throw new RuntimeException("Уже существует");
        } else {
            productRepository.save(product);
        }
        return new ProductResponse(product.getId(), product.getTitle());
    }
}

// сделать для продуктов и категорий базовые rest запросы get put delete и добавить исключения свои
// kafka, reddis, микро
// почитать
// вопросы 10 мин