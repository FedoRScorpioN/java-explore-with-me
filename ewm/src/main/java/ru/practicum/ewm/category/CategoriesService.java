package ru.practicum.ewm.category;

import java.util.Collection;

public interface CategoriesService {
    CategoriesDto addCategory(final NewCategoriesDto category);

    Collection<CategoriesDto> findCategories(Integer from, Integer size);

    CategoriesDto updateCategory(Long id, NewCategoriesDto categoryDto);

    void deleteCategory(Long id);

    CategoriesDto findCategory(Long id);
}