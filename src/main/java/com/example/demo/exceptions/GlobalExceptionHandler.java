package com.example.demo.exceptions;

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
    @ExceptionHandler(RuntimeException.class)
    // указываем, какую ошибку будем обрабатывать
    public ResponseEntity<String> handleRuntime(RuntimeException runtimeException) {  /* в переменную runtimeException будут передаваться исключение,
                                                                                         которое было выброшено со всеми параметрами */
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + runtimeException.getMessage());
    }

    @ExceptionHandler(CategoryExists.class)
    // указываем, какую ошибку будем обрабатывать
    public ResponseEntity<String> handleCategoryExists(CategoryExists categoryExistsException) {  /* в переменную runtimeException будут передаваться исключение,
                                                                                         которое было выброшено со всеми параметрами */
        return ResponseEntity.status(HttpStatus.FOUND).body("Ошибка категории: " + categoryExistsException.getMessage());
    }
}