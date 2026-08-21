package test.junit;

import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createCategory_shouldCreateCategory() {
        // 1. Подготовка запроса
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("Electronics");

        // 2. Создаем сущность Category (НЕ Request)
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        // 3. Мокаем: категории с таким именем нет
        when(categoryRepository.findByName("Electronics"))
                .thenReturn(Optional.empty());  // <-- Пустой Optional

        // 4. Мокаем: при сохранении возвращаем категорию с ID
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        // 5. Выполняем тест
        CategoryResponse response = categoryService.createCategory(request);

        // 6. Проверяем
        assertEquals(1L, response.getId());
        assertEquals("Electronics", response.getName());

        verify(categoryRepository).findByName("Electronics");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    public void getAllCategories_shouldReturnAllCategories() {
        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category2.setId(2L);
        Category category3 = new Category();
        category3.setId(3L);
        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2, category3));
        List<Category> categoriesAll = categoryService.findAllCategories();
        assertEquals(3, categoriesAll.size());
    }

    @Test
    public void updateCategory_shouldUpdateCategory() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Electronics");

        CategoryCreateRequest updateRequest = new CategoryCreateRequest("Cars");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category categoryToSave = invocation.getArgument(0);
                    assertEquals("Cars", categoryToSave.getName());
                    return categoryToSave;
                });

        CategoryResponse response = categoryService.updateCategory(1L, updateRequest);
        assertEquals("Cars", response.getName());
        verify(categoryRepository).save(existingCategory);

        /*
        1. Создаем фальшивую категорию в памяти
           existingCategory = {id: 1, name: "Electronics"}

        2. Создаем запрос на обновление
           updateRequest = {name: "Cars"}

        3. Настраиваем моки:
           - Если вызвали findById(1) → верни existingCategory
           - Если вызвали save(любой) → проверь имя и верни этот же объект

        4. Вызываем метод сервиса:
           updateCategory(1, updateRequest)

           Сервис делает:
           ├── findById(1) → получает existingCategory
           ├── existingCategory.setName("Electronics")
           ├── save(existingCategory) → мок проверяет имя и возвращает existingCategory
           └── возвращает CategoryResponse(existingCategory)

        5. Проверяем:
           ✓ response.getName() = "Cars"
           ✓ save() был вызван с existingCategory
        */
    }

    @Test
    public void deleteCategory_shouldDeleteCategory() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        categoryService.deleteCategory(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    public void findCategoryById_shouldReturnCategory() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Electronics");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        CategoryResponse actualCategory = categoryService.findCategoryById(1L);
        assertEquals(existingCategory.getId(), actualCategory.getId());
        assertEquals(existingCategory.getName(), actualCategory.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }
}
