package com.notbadcode.explorewithme.category;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CategoryMapper {
    public static EventCategory toCategory(CategoryDto categoryDto) {
        return EventCategory.builder()
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(EventCategory category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static List<CategoryDto> toCategoryDto(Page<EventCategory> categories) {
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}
