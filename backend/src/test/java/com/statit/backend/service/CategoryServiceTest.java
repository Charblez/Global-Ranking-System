package com.statit.backend.service;

import com.statit.backend.TestUtils;
import com.statit.backend.model.Category;
import com.statit.backend.model.GlobalBaseline;
import com.statit.backend.model.User;
import com.statit.backend.repository.CategoryRepository;
import com.statit.backend.repository.GlobalBaselineRepository;
import com.statit.backend.repository.ScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest
{
    @Mock private CategoryRepository categoryRepository;
    @Mock private GlobalBaselineRepository globalBaselineRepository;
    @Mock private ScoreRepository scoreRepository;

    @InjectMocks private CategoryService categoryService;

    private User founder;
    private Category existing;
    private UUID categoryId;

    @BeforeEach
    void setUp()
    {
        founder = new User("f", "f@x", "h", LocalDate.of(2000, 1, 1), null);
        TestUtils.setField(founder, "userId", UUID.randomUUID());

        existing = new Category("Cat", "d", "u", null, true, founder);
        categoryId = UUID.randomUUID();
        TestUtils.setField(existing, "categoryId", categoryId);
    }

    @Test
    void createCategorySavesAndCreatesBaseline()
    {
        when(categoryRepository.findByCategoryName("Cat")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            TestUtils.setField(c, "categoryId", categoryId);
            return c;
        });

        Category result = categoryService.createCategory("Cat", "d", "u",
                Arrays.asList("a", "b"), true, founder);

        assertEquals("Cat", result.getName());
        ArgumentCaptor<GlobalBaseline> captor = ArgumentCaptor.forClass(GlobalBaseline.class);
        verify(globalBaselineRepository).save(captor.capture());
        GlobalBaseline saved = captor.getValue();
        assertEquals(0, saved.getSampleSize());
        assertEquals("My Global Ranking Team", saved.getSourceName());
    }

    @Test
    void createCategoryRejectsDuplicate()
    {
        when(categoryRepository.findByCategoryName("Cat")).thenReturn(Optional.of(existing));
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.createCategory("Cat", "d", "u", null, true, founder));
        verify(categoryRepository, never()).save(any());
        verify(globalBaselineRepository, never()).save(any());
    }

    @Test
    void updateCategoryAppliesChanges()
    {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.updateCategory(categoryId, "Cat2", "d2",
                Arrays.asList("x"), "u2", false);

        assertEquals("Cat2", result.getName());
        assertEquals("d2", result.getDescription());
        assertEquals("u2", result.getUnits());
        assertEquals(false, result.getSortOrder());
        assertTrue(result.getTags().contains("x"));
    }

    @Test
    void updateCategoryMissingThrows()
    {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                categoryService.updateCategory(categoryId, "n", "d", null, "u", true));
    }

    @Test
    void getCategoryReturnsCategory()
    {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
        assertSame(existing, categoryService.getCategory(categoryId));
    }

    @Test
    void getCategoryMissingThrows()
    {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> categoryService.getCategory(categoryId));
    }

    @Test
    void getAllCategoriesDelegatesToRepository()
    {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Category> page = new PageImpl<>(List.of(existing), pageable, 1);
        when(categoryRepository.findAllByOrderByCategoryNameAsc(pageable)).thenReturn(page);

        Page<Category> result = categoryService.getAllCategories(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void deleteCategoryRemovesScoresBaselinesAndCategory()
    {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));

        categoryService.deleteCategory(categoryId);

        verify(scoreRepository).deleteAllByCategory(existing);
        verify(globalBaselineRepository).deleteAllByCategory(existing);
        verify(categoryRepository).delete(existing);
    }

    @Test
    void deleteCategoryMissingThrows()
    {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(categoryId));
        verify(categoryRepository, never()).delete(any());
    }
}
