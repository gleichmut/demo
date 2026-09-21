package test.junit;

import com.example.demo.dto.ProductCreateRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class ProductServiceTest {
    @Mock /* Создает фальшивый репозиторий ProductRepository*/
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks /* Создает настоящий ProductService и подставляет в него мок-репозиторий */
    private ProductService productService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createProduct_shouldCreateProduct() {
        ProductCreateRequest productCreateRequest = new ProductCreateRequest();
        productCreateRequest.setTitle("Telephone");
        productCreateRequest.setPrice(new BigDecimal("1000"));
        productCreateRequest.setCategoryId(1L);
        when(productRepository.findByTitle("Telephone")) // Правильно: мокаем Optional
                .thenReturn(Optional.empty());  // <-- Возвращаем пустой Optional
        Category category = new Category(); // Создаем категорию
        category.setId(1L);
        category.setName("Electronics");
        when(categoryRepository.findById(1L)) // Мокаем поиск категории
                .thenReturn(Optional.of(category));
        Product product = new Product(); // Создаем продукт для возврата
        product.setId(1L);
        product.setTitle("Telephone");
        product.setPrice(new BigDecimal("1000"));
        product.setCategory(category);
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);
        ProductResponse response = productService.createProduct(productCreateRequest); // Выполняем тест
        assertEquals(1L, response.getId()); // Проверяем
        assertEquals("Telephone", response.getTitle());
    }

    @Test
    public void getAllProducts_shouldReturnAllProducts() {
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        Product product3 = new Product();
        product3.setId(3L);
        when(productRepository.findAll()).thenReturn(List.of(product1, product2, product3));
        List<ProductResponse> productsAll = productService.findAllProducts();
        assertEquals(3, productsAll.size());
    }

    @Test
    public void updateProduct_shouldUpdateProduct() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setTitle("Telephone");
        existingProduct.setPrice(BigDecimal.valueOf(1000));

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        ProductCreateRequest updateRequest = new ProductCreateRequest(
                "Smartphone",
                BigDecimal.valueOf(1500),
                1L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product productToSave = invocation.getArgument(0);
                    assertEquals("Smartphone", productToSave.getTitle());
                    assertEquals(BigDecimal.valueOf(1500), productToSave.getPrice());
                    return productToSave;
                });
        ProductResponse response = productService.updateProduct(1L, updateRequest);
        assertEquals("Smartphone", response.getTitle());
        assertEquals(BigDecimal.valueOf(1500), response.getPrice());
        verify(productRepository).save(existingProduct);
    }

    @Test
    public void deleteProduct_shouldDeleteProduct() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        when(productRepository.findById(existingProduct.getId())).thenReturn(Optional.of(existingProduct));
        productService.deleteProduct(existingProduct.getId());
        verify(productRepository, times(1)).deleteById(existingProduct.getId());
    }

    @Test
    public void findProductById_shouldReturnProduct() {
        Product expectedProduct = new Product();
        expectedProduct.setId(1L);
        expectedProduct.setTitle("Telephone");
        when(productRepository.findById(expectedProduct.getId())).thenReturn(Optional.of(expectedProduct));
        ProductResponse actualProduct = productService.findProductById(expectedProduct.getId());
        assertEquals(expectedProduct.getId(), actualProduct.getId());
        assertEquals(expectedProduct.getTitle(), actualProduct.getTitle());
        verify(productRepository, times(1)).findById(expectedProduct.getId());
    }
}
