package ru.practicum.explorewithme.category;

import ru.practicum.explorewithme.error.NotFoundException;
import ru.practicum.explorewithme.event.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventService eventService;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, @Lazy EventService eventService) {
        this.categoryRepository = categoryRepository;
        this.eventService = eventService;
    }

    public EventCategory getCategoryOr404Error(Long categoryId) {
        log.debug("Load category id={}", categoryId);
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        EventCategory category = categoryRepository.save(CategoryMapper.toCategory(categoryDto));
        log.debug("Category id={} has been created", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        EventCategory category = getCategoryOr404Error(categoryId);
        if (!eventService.existsByCategoryId(categoryId)) {
            categoryRepository.delete(category);
            log.debug("Category id={} has been deleted", categoryId);
        }
        log.debug("Category id={} is used", categoryId);
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        EventCategory category = getCategoryOr404Error(categoryDto.getId());
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        log.debug("Category id={} has been updated", category.getId());
        return CategoryMapper.toCategoryDto(category);
    }
}
