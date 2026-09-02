package test.integration;

import com.example.demo.DemoApplication;
import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.exceptions.CategoryNotFound;
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

    @BeforeEach
    public void setup() {
        categoryRepository.deleteAll();
    }

    @Test
    public void createCategory_shouldSaveCategory() {
        CategoryCreateRequest request = createRequest("Electronics");
        CategoryResponse response = categoryService.createCategory(request);
        Assertions.assertAll(
                () -> Assertions.assertNotNull(request.getName())
        );
    }

    public CategoryCreateRequest createRequest(String name) {
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName(name);
        return request;
    }

    @Test
    public void findCategoryById_shouldReturnCategory() {
        CategoryCreateRequest request = createRequest("Electronics");
        CategoryResponse response = categoryService.createCategory(request);
        CategoryResponse getCategory = categoryService.findCategoryById(response.getId());
        Assertions.assertAll(
                () -> Assertions.assertNotNull(getCategory)
        );
    }

    @Test
    public void findAllCategories_shouldReturnListOfCategories() {
        CategoryCreateRequest request1 = createRequest("Electronics");
        CategoryResponse response1 = categoryService.createCategory(request1);
        CategoryCreateRequest request2 = createRequest("Used Electronics");
        CategoryResponse response2 = categoryService.createCategory(request2);
        List<CategoryResponse> categoryResponseList = categoryService.findAllCategories();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(categoryResponseList)
        );
    }

    @Test
    public void updateCategory_shouldUpdateCategory() {
        CategoryCreateRequest request = createRequest("Electronics");
        CategoryResponse response = categoryService.createCategory(request);
        request.setName("Used Electronics");
        categoryService.updateCategory(response.getId(), request);
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(request.getName(), response.getName())
        );
    }

    @Test
    public void deleteCategory_shouldDeleteCategory() {
        CategoryCreateRequest request = createRequest("Electronics");
        CategoryResponse response = categoryService.createCategory(request);
        CategoryResponse removableCategory = categoryService.findCategoryById(response.getId());
        categoryService.deleteCategory(removableCategory.getId());
        Assertions.assertAll(
                () -> Assertions.assertThrows(CategoryNotFound.class,
                        () -> categoryService.findCategoryById(removableCategory.getId()))
        );
    }
}
