package test.integration;

import com.example.demo.DemoApplication;
import com.example.demo.dto.ProductCreateRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exceptions.ProductNotFound;
import com.example.demo.mapper.ProductMapper;
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
    @Autowired
    private ProductMapper productMapper;

    private Category category;

    @BeforeEach
    public void setup() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        category = new Category();
        category.setName("Electronics");
        category = categoryRepository.save(category);
    }

    // ---------- helpers ----------

    private ProductCreateRequest createRequest(String title, BigDecimal price, Long categoryId) {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setTitle(title);
        request.setPrice(price);
        request.setCategoryId(categoryId);
        return request;
    }

    private Product createEntity(String title, BigDecimal price) {
        Product product = new Product();
        product.setTitle(title);
        product.setPrice(price);
        product.setCategory(category);
        return product;
    }

    // ---------- service tests ----------

    @Test
    public void createProduct_shouldSaveProduct() {
        ProductCreateRequest request = createRequest("Notebook", new BigDecimal("100000"), category.getId());

        ProductResponse response = productService.createProduct(request);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertNotNull(response.getId()),
                () -> Assertions.assertEquals("Notebook", response.getTitle()),
                () -> Assertions.assertEquals(0, new BigDecimal("100000").compareTo(response.getPrice())),
                () -> Assertions.assertEquals(category.getId(), response.getCategoryId())
        );
    }

    @Test
    public void findProductById_shouldReturnProduct() {
        ProductResponse created = productService.createProduct(
                createRequest("Notebook", new BigDecimal("100000"), category.getId()));

        ProductResponse found = productService.findProductById(created.getId());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(found),
                () -> Assertions.assertEquals(created.getId(), found.getId()),
                () -> Assertions.assertEquals("Notebook", found.getTitle()),
                () -> Assertions.assertEquals(0, new BigDecimal("100000").compareTo(found.getPrice())),
                () -> Assertions.assertEquals(category.getId(), found.getCategoryId())
        );
    }

    @Test
    public void findAllProducts_shouldReturnListOfProducts() {
        productService.createProduct(createRequest("Notebook", new BigDecimal("100000"), category.getId()));
        productService.createProduct(createRequest("Telephone", new BigDecimal("20000"), category.getId()));

        List<ProductResponse> products = productService.findAllProducts();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(products),
                () -> Assertions.assertEquals(2, products.size()),
                () -> Assertions.assertTrue(products.stream()
                        .anyMatch(p -> "Notebook".equals(p.getTitle()))),
                () -> Assertions.assertTrue(products.stream()
                        .anyMatch(p -> "Telephone".equals(p.getTitle())))
        );
    }

    @Test
    public void updateProduct_shouldUpdateProduct() {
        ProductResponse created = productService.createProduct(
                createRequest("Notebook", new BigDecimal("100000"), category.getId()));

        ProductCreateRequest updateRequest =
                createRequest("Ultra Notebook", new BigDecimal("150000"), category.getId());

        ProductResponse updated = productService.updateProduct(created.getId(), updateRequest);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(updated),
                () -> Assertions.assertEquals(created.getId(), updated.getId()),
                () -> Assertions.assertEquals("Ultra Notebook", updated.getTitle()),
                () -> Assertions.assertEquals(0, new BigDecimal("150000").compareTo(updated.getPrice())),
                () -> Assertions.assertEquals(category.getId(), updated.getCategoryId())
        );
    }

    @Test
    public void deleteProduct_shouldDeleteProduct() {
        ProductResponse created = productService.createProduct(
                createRequest("Notebook", new BigDecimal("100000"), category.getId()));

        productService.deleteProduct(created.getId());

        Assertions.assertAll(
                () -> Assertions.assertThrows(ProductNotFound.class,
                        () -> productService.findProductById(created.getId()))
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

    // ---------- mapper tests ----------

    @Test
    public void mapper_toResponse_shouldMapAllFieldsAndCategoryId() {
        Product entity = createEntity("Notebook", new BigDecimal("100000"));
        entity.setId(7L);

        ProductResponse response = productMapper.toResponse(entity);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(7L, response.getId()),
                () -> Assertions.assertEquals("Notebook", response.getTitle()),
                () -> Assertions.assertEquals(0, new BigDecimal("100000").compareTo(response.getPrice())),
                () -> Assertions.assertEquals(category.getId(), response.getCategoryId())
        );
    }

    @Test
    public void mapper_toResponse_shouldHandleNullCategory() {
        Product entity = createEntity("Notebook", new BigDecimal("100000"));
        entity.setId(7L);
        entity.setCategory(null);

        ProductResponse response = productMapper.toResponse(entity);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(7L, response.getId()),
                () -> Assertions.assertEquals("Notebook", response.getTitle()),
                () -> Assertions.assertNull(response.getCategoryId())
        );
    }

    @Test
    public void mapper_toEntity_fromCreateRequest_shouldIgnoreIdAndCategory() {
        ProductCreateRequest request = createRequest("Notebook", new BigDecimal("100000"), category.getId());

        Product entity = productMapper.toEntity(request);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(entity),
                () -> Assertions.assertNull(entity.getId(), "id должен быть проигнорирован"),
                () -> Assertions.assertNull(entity.getCategory(), "category должен быть проигнорирован"),
                () -> Assertions.assertEquals("Notebook", entity.getTitle()),
                () -> Assertions.assertEquals(0, new BigDecimal("100000").compareTo(entity.getPrice()))
        );
    }

    @Test
    public void mapper_toEntity_fromResponse_shouldIgnoreIdAndCategory() {
        ProductResponse response = new ProductResponse();
        response.setId(99L);
        response.setTitle("Notebook");
        response.setPrice(new BigDecimal("100000"));
        response.setCategoryId(category.getId());

        Product entity = productMapper.toEntity(response);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(entity),
                () -> Assertions.assertNull(entity.getId(), "id должен быть проигнорирован"),
                () -> Assertions.assertNull(entity.getCategory(), "category должен быть проигнорирован"),
                () -> Assertions.assertEquals("Notebook", entity.getTitle()),
                () -> Assertions.assertEquals(0, new BigDecimal("100000").compareTo(entity.getPrice()))
        );
    }

    @Test
    public void mapper_toRequest_shouldMapAllFields() {
        Product entity = createEntity("Notebook", new BigDecimal("100000"));

        ProductCreateRequest request = productMapper.toRequest(entity);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(request),
                () -> Assertions.assertEquals("Notebook", request.getTitle()),
                () -> Assertions.assertEquals(0, new BigDecimal("100000").compareTo(request.getPrice())),
                () -> Assertions.assertEquals(category.getId(), request.getCategoryId())
        );
    }

    @Test
    public void mapper_updateEntity_shouldUpdateFieldsButKeepIdAndCategory() {
        Product entity = createEntity("Notebook", new BigDecimal("100000"));
        entity.setId(11L);
        Category originalCategory = entity.getCategory();

        ProductCreateRequest updateRequest =
                createRequest("Ultra Notebook", new BigDecimal("150000"), null);

        productMapper.updateEntity(updateRequest, entity);

        Assertions.assertAll(
                () -> Assertions.assertEquals(11L, entity.getId(), "id не должен измениться"),
                () -> Assertions.assertEquals(originalCategory, entity.getCategory(),
                        "category не должна перезаписываться"),
                () -> Assertions.assertEquals("Ultra Notebook", entity.getTitle()),
                () -> Assertions.assertEquals(0, new BigDecimal("150000").compareTo(entity.getPrice()))
        );
    }

    @Test
    public void mapper_toResponseList_shouldMapEveryElement() {
        List<Product> entities = List.of(
                createEntity("Notebook", new BigDecimal("100000")),
                createEntity("Telephone", new BigDecimal("20000")),
                createEntity("Tablet", new BigDecimal("50000"))
        );

        List<ProductResponse> responses = productMapper.toResponseList(entities);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(responses),
                () -> Assertions.assertEquals(3, responses.size()),
                () -> Assertions.assertEquals("Notebook", responses.get(0).getTitle()),
                () -> Assertions.assertEquals("Telephone", responses.get(1).getTitle()),
                () -> Assertions.assertEquals("Tablet", responses.get(2).getTitle()),
                () -> Assertions.assertTrue(responses.stream()
                        .allMatch(r -> category.getId().equals(r.getCategoryId())))
        );
    }

    @Test
    public void mapper_toResponseList_shouldReturnEmptyListForEmptyInput() {
        List<ProductResponse> responses = productMapper.toResponseList(List.of());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(responses),
                () -> Assertions.assertTrue(responses.isEmpty())
        );
    }

    @Test
    public void mapper_toResponse_shouldReturnNullForNullInput() {
        Assertions.assertNull(productMapper.toResponse(null));
    }

    @Test
    public void mapper_toEntity_fromCreateRequest_shouldReturnNullForNullInput() {
        Assertions.assertNull(productMapper.toEntity((ProductCreateRequest) null));
    }
}