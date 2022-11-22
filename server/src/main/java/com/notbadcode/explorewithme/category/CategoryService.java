package com.notbadcode.explorewithme.category;

import java.util.List;

public interface CategoryService {
    EventCategory getCategoryById(Long categoryId);

    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(CategoryDto categoryDto);

    List<CategoryDto> findAllCategories(int from, int size);

    CategoryDto findCategoriesById(Long catId);
}
