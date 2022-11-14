package ru.practicum.explorewithme.category;

import lombok.experimental.UtilityClass;

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
}
