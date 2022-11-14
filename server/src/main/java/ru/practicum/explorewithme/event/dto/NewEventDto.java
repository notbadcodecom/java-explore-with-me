package ru.practicum.explorewithme.event.dto;

import ru.practicum.explorewithme.event.DateTimeStartHourValidation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank(message = "Event annotation is required")
    @Size(min = 20, max = 2000, message = "Size of annotation should be from {min} to {max} characters")
    String annotation;

    @NotBlank(message = "Event title is required")
    @Size(min = 3, max = 120, message = "Size of titles should be from {min} to {max} characters")
    String title;

    @NotBlank(message = "Event description is required")
    @Size(min = 20, max = 7000, message = "Size of description should be from {min} to {max} characters")
    String description;

    @NotNull(message = "Event start datetime is required")
    @DateTimeStartHourValidation(value = 2,
            message = "Start datetime should be in {value} hours")
    LocalDateTime eventDate;

    @NotNull(message = "Event location is required")
    LocationDto location;

    @NotNull(message = "Category id is required")
    Long category;

    @NotNull(message = "Payment information about event is required")
    Boolean paid;

    Integer participantLimit;

    boolean requestModeration;
}
