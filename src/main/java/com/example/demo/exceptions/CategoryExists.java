package com.example.demo.exceptions;

public class CategoryExists extends RuntimeException {
    public CategoryExists(String categoryName) {
        super(categoryName);
    }
}
