package com.notbadcode.explorewithme.location.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationFullDto {
    Long id;

    @NotBlank(message = "name is required")
    String name;

    @NotBlank(message = "type is required")
    String type;

    @NotNull(message = "latitude is required")
    Double lat;

    @NotNull(message = "longitude is required")
    Double lon;

    @Positive
    @NotNull(message = "radius is required")
    Long radius;
}
