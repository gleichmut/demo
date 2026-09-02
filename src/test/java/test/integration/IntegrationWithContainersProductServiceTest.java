package test.integration;

import com.example.demo.DemoApplication;
import com.example.demo.dto.ProductCreateRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.exceptions.ProductNotFound;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(classes = DemoApplication.class)
@ActiveProfiles("test")
@Transactional
@Testcontainers
public class IntegrationWithContainersProductServiceTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("product_test_db")
            .withUsername("test")
            .withPassword("test");
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    public void setup() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        category = new Category();
        category.setName("Electronics");
        category = categoryRepository.save(category);
    }

    @Test
    public void createProduct_shouldSaveProduct() {
        ProductCreateRequest request = createRequest("Notebook", new BigDecimal(100000), category.getId());
        ProductResponse response = productService.createProduct(request);
        Assertions.assertNotNull(response.getTitle());
    }

    public ProductCreateRequest createRequest(String title, BigDecimal price, Long categoryId) {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setTitle(title);
        request.setPrice(price);
        request.setCategoryId(categoryId);
        return request;
    }

    @Test
    public void findProductById_shouldReturnProduct() {
        ProductCreateRequest request = createRequest("Notebook", new BigDecimal(100000), category.getId());
        ProductResponse response = productService.createProduct(request);
        ProductResponse getProduct = productService.findProductById(response.getId());
        Assertions.assertAll(
                () -> Assertions.assertNotNull(getProduct),
                () -> Assertions.assertEquals(getProduct.getTitle(), request.getTitle())
        );
    }

    @Test
    public void findAllProducts_shouldReturnListOfProducts() {
        ProductCreateRequest request1 = createRequest("Notebook", new BigDecimal(100000), category.getId());
        ProductResponse response1 = productService.createProduct(request1);
        ProductCreateRequest request2 = createRequest("Telephone", new BigDecimal(20000), category.getId());
        ProductResponse response2 = productService.createProduct(request2);
        List<ProductResponse> productResponseList = productService.findAllProducts();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(productResponseList)
        );
    }


    @Test
    public void updateProduct_shouldUpdateProduct() {
        ProductCreateRequest request = createRequest("Notebook", new BigDecimal(100000), category.getId());
        ProductResponse response = productService.createProduct(request);
        request.setTitle("Ultra Notebook");
        productService.updateProduct(response.getId(), request);
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(response.getTitle(), request.getTitle())
        );
    }

    @Test
    public void deleteProduct_shouldDeleteProduct() {
        ProductCreateRequest request = createRequest("Notebook", new BigDecimal(100000), category.getId());
        ProductResponse response = productService.createProduct(request);
        ProductResponse deleteProduct = productService.findProductById(response.getId());
        productService.deleteProduct(deleteProduct.getId());
        Assertions.assertAll(
                () -> Assertions.assertThrows(ProductNotFound.class,
                        () -> productService.findProductById(deleteProduct.getId()))
        );
    }


    @Test
    public void deleteAllProducts_shouldDeleteAllProducts() {
        productService.createProduct(createRequest("Notebook", new BigDecimal(100000), category.getId()));
        productService.createProduct(createRequest("Telephone", new BigDecimal(20000), category.getId()));
        productService.deleteAllProducts();
        Assertions.assertAll(
                () -> Assertions.assertThrows(ProductNotFound.class, () -> productService.findAllProducts())
        );
    }
}
