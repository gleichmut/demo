package com.example.demo.dto;

import com.example.demo.entity.Product;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductResponse {
    private Long id;
    private String title;
    private BigDecimal price;
    private Long categoryId;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.price = product.getPrice();
        this.categoryId = product.getCategory() != null ?
                product.getCategory().getId() : null;
    }
}
