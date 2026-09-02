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
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@SpringBootTest(classes = DemoApplication.class)
@ActiveProfiles("test")
@Transactional
public class IntegrationServiceTest {
    @Autowired /*типа вместо конструктора*/
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
        category.setName("TestCategory");
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
}
