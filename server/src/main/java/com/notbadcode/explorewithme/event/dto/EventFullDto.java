package com.notbadcode.explorewithme.event.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.notbadcode.explorewithme.category.CategoryDto;
import com.notbadcode.explorewithme.event.model.EventState;
import com.notbadcode.explorewithme.user.dto.UserShortDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;

    String annotation;

    String title;

    String description;

    EventState state;

    LocalDateTime createdOn;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDateTime publishedOn;

    LocalDateTime eventDate;

    UserShortDto initiator;

    LocationDto location;

    CategoryDto category;

    int confirmedRequests;

    Boolean paid;

    Boolean requestModeration;

    int participantLimit;

    int views;
}
