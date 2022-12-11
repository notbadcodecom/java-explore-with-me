package com.notbadcode.explorewithme.event.dto;

import com.notbadcode.explorewithme.location.dto.LocationShortDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminUpdateEventDto {
    String annotation;

    String title;

    String description;

    LocalDateTime eventDate;

    LocationShortDto location;

    Long category;

    Boolean paid;

    Boolean requestModeration;

    Integer participantLimit;
}
