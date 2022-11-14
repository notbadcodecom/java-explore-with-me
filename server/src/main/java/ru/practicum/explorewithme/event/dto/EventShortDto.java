package ru.practicum.explorewithme.event.dto;

import ru.practicum.explorewithme.category.CategoryDto;
import ru.practicum.explorewithme.user.dto.UserShortDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;

    String annotation;

    String title;

    LocalDateTime eventDate;

    UserShortDto initiator;

    CategoryDto category;

    int confirmedRequests;

    Boolean paid;

    int views;
}
