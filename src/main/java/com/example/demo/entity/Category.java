package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor // создает полный конструктор
@NoArgsConstructor // пустой конструктор
@Getter
@Setter
//@Data // автоматически добавляет геттеры, сеттеры, equals, hash_code, toString
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
}
