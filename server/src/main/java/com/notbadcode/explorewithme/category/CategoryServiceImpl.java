package com.notbadcode.explorewithme.category;

import com.notbadcode.explorewithme.error.NotFoundException;
import com.notbadcode.explorewithme.util.SizeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public EventCategory getCategoryById(Long categoryId) {
        log.debug("Load category id={}", categoryId);
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        EventCategory category = categoryRepository.save(CategoryMapper.toCategory(categoryDto));
        log.debug("Category id={} has been created", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        EventCategory category = getCategoryById(categoryId);
        if (!categoryRepository.existsEventsByCategoryId(categoryId)) {
            categoryRepository.delete(category);
            log.debug("Category id={} has been deleted", categoryId);
        }
        log.debug("Category id={} is used", categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        EventCategory category = getCategoryById(categoryDto.getId());
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        log.debug("Category id={} has been updated", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> findAllCategories(int from, int size) {
        Page<EventCategory> categories = categoryRepository.findAll(SizeRequest.of(from, size));
        log.debug("Found {} categories", categories.getSize());
        return CategoryMapper.toCategoryDto(categories);
    }

    @Override
    public CategoryDto findCategoriesById(Long catId) {
        log.debug("Category id={} was found", catId);
        return CategoryMapper.toCategoryDto(getCategoryById(catId));
    }
}
