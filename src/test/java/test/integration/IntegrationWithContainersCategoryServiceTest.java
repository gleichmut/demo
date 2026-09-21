package test.integration;

import com.example.demo.DemoApplication;
import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.exceptions.CategoryNotFound;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.CategoryService;
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

import java.util.List;

@SpringBootTest(classes = DemoApplication.class)
@ActiveProfiles("test")
@Transactional
@Testcontainers
public class IntegrationWithContainersCategoryServiceTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("product_test_db")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @BeforeEach
    public void setup() {
        categoryRepository.deleteAll();
    }

    // ---------- helpers ----------

    private CategoryCreateRequest createRequest(String name) {
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName(name);
        return request;
    }

    private Category createEntity(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    // ---------- service tests ----------

    @Test
    public void createCategory_shouldSaveCategory() {
        CategoryCreateRequest request = createRequest("Electronics");

        CategoryResponse response = categoryService.createCategory(request);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertNotNull(response.getId()),
                () -> Assertions.assertEquals("Electronics", response.getName())
        );
    }

    @Test
    public void findCategoryById_shouldReturnCategory() {
        CategoryResponse created = categoryService.createCategory(createRequest("Electronics"));

        CategoryResponse found = categoryService.findCategoryById(created.getId());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(found),
                () -> Assertions.assertEquals(created.getId(), found.getId()),
                () -> Assertions.assertEquals("Electronics", found.getName())
        );
    }

    @Test
    public void findAllCategories_shouldReturnListOfCategories() {
        categoryService.createCategory(createRequest("Electronics"));
        categoryService.createCategory(createRequest("Used Electronics"));

        List<CategoryResponse> categories = categoryService.findAllCategories();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(categories),
                () -> Assertions.assertEquals(2, categories.size()),
                () -> Assertions.assertTrue(categories.stream()
                        .anyMatch(c -> "Electronics".equals(c.getName()))),
                () -> Assertions.assertTrue(categories.stream()
                        .anyMatch(c -> "Used Electronics".equals(c.getName())))
        );
    }

    @Test
    public void updateCategory_shouldUpdateCategory() {
        CategoryResponse created = categoryService.createCategory(createRequest("Electronics"));

        CategoryCreateRequest updateRequest = createRequest("Used Electronics");
        CategoryResponse updated = categoryService.updateCategory(created.getId(), updateRequest);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(updated),
                () -> Assertions.assertEquals(created.getId(), updated.getId()),
                () -> Assertions.assertEquals("Used Electronics", updated.getName())
        );
    }

    @Test
    public void deleteCategory_shouldDeleteCategory() {
        CategoryResponse created = categoryService.createCategory(createRequest("Electronics"));

        categoryService.deleteCategory(created.getId());

        Assertions.assertAll(
                () -> Assertions.assertThrows(CategoryNotFound.class,
                        () -> categoryService.findCategoryById(created.getId()))
        );
    }

    // ---------- mapper tests ----------

    @Test
    public void mapper_toResponse_shouldMapAllFields() {
        Category entity = createEntity("Electronics");
        entity.setId(42L);

        CategoryResponse response = categoryMapper.toResponse(entity);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(response),
                () -> Assertions.assertEquals(42L, response.getId()),
                () -> Assertions.assertEquals("Electronics", response.getName())
        );
    }

    @Test
    public void mapper_toEntity_shouldMapAllFields() {
        CategoryCreateRequest request = createRequest("Electronics");

        Category entity = categoryMapper.toEntity(request);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(entity),
                () -> Assertions.assertNull(entity.getId()),
                () -> Assertions.assertEquals("Electronics", entity.getName())
        );
    }

    @Test
    public void mapper_toResponseList_shouldMapEveryElement() {
        List<Category> entities = List.of(
                createEntity("Electronics"),
                createEntity("Used Electronics"),
                createEntity("Books")
        );

        List<CategoryResponse> responses = categoryMapper.toResponseList(entities);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(responses),
                () -> Assertions.assertEquals(entities.size(), responses.size()),
                () -> Assertions.assertEquals("Electronics", responses.get(0).getName()),
                () -> Assertions.assertEquals("Used Electronics", responses.get(1).getName()),
                () -> Assertions.assertEquals("Books", responses.get(2).getName())
        );
    }

    @Test
    public void mapper_toResponseList_shouldReturnEmptyListForEmptyInput() {
        List<CategoryResponse> responses = categoryMapper.toResponseList(List.of());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(responses),
                () -> Assertions.assertTrue(responses.isEmpty())
        );
    }

    @Test
    public void mapper_toResponse_shouldReturnNullForNullInput() {
        Assertions.assertNull(categoryMapper.toResponse(null));
    }

    @Test
    public void mapper_toEntity_shouldReturnNullForNullInput() {
        Assertions.assertNull(categoryMapper.toEntity(null));
    }

    /**
     * Проверка, что маппер, внедрённый в контекст, реально используется сервисом:
     * результат сервиса должен совпадать с результатом прямого маппинга сущности.
     */
    @Test
    public void service_shouldUseMapperConsistently() {
        CategoryResponse created = categoryService.createCategory(createRequest("Electronics"));

        Category entity = categoryRepository.findById(created.getId()).orElseThrow();
        CategoryResponse mapped = categoryMapper.toResponse(entity);

        Assertions.assertAll(
                () -> Assertions.assertEquals(mapped.getId(), created.getId()),
                () -> Assertions.assertEquals(mapped.getName(), created.getName())
        );
    }
}