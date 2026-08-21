package com.example.demo.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// контроллер, который наблюдает за всем приложением, и если в каком-то из бинов произойдет ошибка,
// то она будет обработана в данном контроллере
// в читаемом виде возвращается информация в постмане
// исключения не ломают приложение, но RestControllerAdvice нужен для того, чтобы информировать клиента об ошибке
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private void logMessage(Object object, String message) {
        String className = object.getClass().getSimpleName();
        log.error("Ошибка в классе {}: {}", className, message);
    }

    @ExceptionHandler(RuntimeException.class)
    // указываем, какую ошибку будем обрабатывать
    public ResponseEntity<String> handleRuntime(RuntimeException runtimeException) {
        log.error("Ошибка в {}: ", runtimeException.getClass().getName(), runtimeException);
        /* в переменную runtimeException будут передаваться исключение, которое было выброшено со всеми параметрами */
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Внутренняя ошибка сервера: " + runtimeException.getMessage());
    }

    @ExceptionHandler(CategoryExists.class)
    // указываем, какую ошибку будем обрабатывать
    public ResponseEntity<String> handleCategoryExists(CategoryExists categoryExistsException) {
        /* в переменную runtimeException будут передаваться исключение, которое было выброшено со всеми параметрами */
        logMessage(this, categoryExistsException.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Ошибка: " + categoryExistsException.getMessage());
    }

    @ExceptionHandler(CategoryNotFound.class)
    // указываем, какую ошибку будем обрабатывать
    public ResponseEntity<String> handleCategoryNotFound(CategoryNotFound categoryNotFoundException) {
        /* в переменную runtimeException будут передаваться исключение, которое было выброшено со всеми параметрами */
        logMessage(this, categoryNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Ошибка: " + categoryNotFoundException.getMessage());
    }

    @ExceptionHandler(ProductExists.class)
    public ResponseEntity<String> handleProductExists(ProductExists productExistsException) {
        logMessage(this, productExistsException.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: " + productExistsException.getMessage());
    }

    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFound productNotFoundException) {
        logMessage(this, productNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ошибка: " + productNotFoundException.getMessage());
    }
}