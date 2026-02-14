package ru.practicum.ewm.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.EventsRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriesServiceImplTest {

    @Mock
    private CategoriesRepository categoriesRepository;
    @Mock
    private EventsRepository eventsRepository;

    @InjectMocks
    private CategoriesServiceImpl categoriesService;

    private Categories testCategory;
    private NewCategoriesDto newCategoryDto;

    @BeforeEach
    void setUp() {
        testCategory = new Categories();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        newCategoryDto = new NewCategoriesDto();
        newCategoryDto.setName("New Category");
    }

    @Test
    @DisplayName("addCategory: Should create category successfully")
    void addCategory_ShouldCreateCategorySuccessfully() {
        // Arrange
        when(categoriesRepository.save(any(Categories.class))).thenAnswer(invocation -> {
            Categories saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        CategoriesDto result = categoriesService.addCategory(newCategoryDto);

        // Assert
        assertNotNull(result);
        assertEquals("New Category", result.getName());
        verify(categoriesRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("deleteCategory: Should delete category successfully")
    void deleteCategory_ShouldDeleteSuccessfully() {
        // Arrange
        when(categoriesRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(eventsRepository.countByCategoriesId(1L)).thenReturn(0L);
        doNothing().when(categoriesRepository).delete(testCategory);

        // Act
        categoriesService.deleteCategory(1L);

        // Assert
        verify(categoriesRepository, times(1)).delete(testCategory);
    }

    @Test
    @DisplayName("deleteCategory: Should throw NotFoundException when category not found")
    void deleteCategory_ShouldThrowNotFoundException_WhenNotFound() {
        // Arrange
        when(categoriesRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> categoriesService.deleteCategory(999L));
        verify(categoriesRepository, never()).delete(any());
    }

    @Test
    @DisplayName("deleteCategory: Should throw ConflictException when category has events")
    void deleteCategory_ShouldThrowConflictException_WhenHasEvents() {
        // Arrange
        when(categoriesRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(eventsRepository.countByCategoriesId(1L)).thenReturn(5L);

        // Act & Assert
        assertThrows(ConflictException.class, () -> categoriesService.deleteCategory(1L));
        verify(categoriesRepository, never()).delete(any());
    }

    @Test
    @DisplayName("updateCategory: Should update category successfully")
    void updateCategory_ShouldUpdateSuccessfully() {
        // Arrange
        NewCategoriesDto updateDto = new NewCategoriesDto();
        updateDto.setName("Updated Category");

        when(categoriesRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoriesRepository.save(any())).thenReturn(testCategory);

        // Act
        CategoriesDto result = categoriesService.updateCategory(1L, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Category", testCategory.getName());
    }

    @Test
    @DisplayName("updateCategory: Should throw NotFoundException when category not found")
    void updateCategory_ShouldThrowNotFoundException_WhenNotFound() {
        // Arrange
        NewCategoriesDto updateDto = new NewCategoriesDto();
        updateDto.setName("Updated Category");

        when(categoriesRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> categoriesService.updateCategory(999L, updateDto));
    }

    @Test
    @DisplayName("findCategories: Should return paginated categories")
    void findCategories_ShouldReturnPaginatedCategories() {
        // Arrange
        when(categoriesRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testCategory)));

        // Act
        Collection<CategoriesDto> result = categoriesService.findCategories(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findCategory: Should return category by id")
    void findCategory_ShouldReturnCategory() {
        // Arrange
        when(categoriesRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        CategoriesDto result = categoriesService.findCategory(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Category", result.getName());
    }

    @Test
    @DisplayName("findCategory: Should throw NotFoundException when category not found")
    void findCategory_ShouldThrowNotFoundException_WhenNotFound() {
        // Arrange
        when(categoriesRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> categoriesService.findCategory(999L));
    }
}
