package com.notbadcode.explorewithme.location;

import com.notbadcode.explorewithme.location.dto.LocationFullDto;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class LocationMapper {
    public static Location toLocation(LocationFullDto locationDto) {
        return Location.builder()
                .name(locationDto.getName())
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .type(LocationType.from(locationDto.getType()))
                .radius(locationDto.getRadius())
                .build();
    }

    public static LocationFullDto toLocationFullDto(Location location) {
        return LocationFullDto.builder()
                .id(location.getId())
                .name(location.getName())
                .lat(location.getLat())
                .lon(location.getLon())
                .type(location.getType().name())
                .radius(location.getRadius())
                .build();
    }

    public static List<LocationFullDto> toLocationFullDto(Page<Location> locations) {
        return locations.stream()
                .map(LocationMapper::toLocationFullDto)
                .collect(Collectors.toList());
    }
}
