package ru.practicum.explorewithme.event.dto;

import ru.practicum.explorewithme.event.DateTimeStartHourValidation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventDto {
    @NotNull(message = "Event id (eventId) is required")
    Long eventId;

    String annotation;

    String title;

    String description;

    @DateTimeStartHourValidation(value = 2,
            message = "Start datetime should be in {value} hours")
    LocalDateTime eventDate;

    Long category;

    Boolean paid;

    Integer participantLimit;
}
