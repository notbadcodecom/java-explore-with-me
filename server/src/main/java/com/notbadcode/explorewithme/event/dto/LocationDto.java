package com.notbadcode.explorewithme.event.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@ToString
public class LocationDto {
    @NotNull(message = "Lat in event location is required")
    Double lat;

    @NotNull(message = "Lon in event location is required")
    Double lon;
}
