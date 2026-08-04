package com.example.demo.service;

import com.example.demo.dto.ProductCreateRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.exceptions.ProductExists;
import com.example.demo.exceptions.ProductNotFound;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setTitle(request.getTitle());

        if (productRepository.existsByTitle(request.getTitle())) {
            throw new ProductExists("Продукт: " + request.getTitle() + " уже существует.");
        } else {
            productRepository.save(product);
        }
        return new ProductResponse(product.getId(), product.getTitle());
    }

    public List<Product> getAllProducts() {
        if (productRepository.findAll().isEmpty()) {
            throw new ProductNotFound("Список  пуст или категории не найдены.");
        }
        return productRepository.findAll();
    }

    public ProductResponse updateProduct(Long id, ProductCreateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductExists("Продукт: " + request.getTitle() + " не найден."));
        product.setTitle(request.getTitle());
        Product updatesProduct = productRepository.save(product);
        return new ProductResponse(updatesProduct.getId(), updatesProduct.getTitle());
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductExists("Продукт с id = " + id + " не найден.");
        }
        productRepository.deleteById(id);
    }
}



// сделать для продуктов и категорий базовые rest запросы get put delete и добавить исключения свои
// kafka, reddis, микро
// почитать
// вопросы 10 мин