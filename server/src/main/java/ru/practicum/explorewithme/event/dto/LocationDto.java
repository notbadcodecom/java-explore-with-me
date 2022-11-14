package ru.practicum.explorewithme.event.dto;

import lombok.*;

@Getter
@Builder
@ToString
public class LocationDto {
    Double lat;

    Double lon;
}
