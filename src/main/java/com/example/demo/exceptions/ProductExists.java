package com.example.demo.exceptions;

public class ProductExists extends RuntimeException {
    public ProductExists(String message) {
        super(message);
    }
}
